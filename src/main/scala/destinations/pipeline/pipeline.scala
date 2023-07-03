package destinations.pipeline

import cats.effect.IO
import fs2.*
import destinations.model.Flight
import destinations.model.parser.parseFlightOption
import destinations.pipeline.load.insertFlight
import fs2.io.file.{Files, Path}
import fs2.{Chunk, Pipe, Pure, Stream, text}

import java.io.{File, FileInputStream, FileOutputStream, InputStream}
import java.net.URL
import java.nio.file.Path
import java.util.zip.ZipInputStream

object pipeline {
  def readZippedUrl(target: String): Stream[IO, Byte] =
    val download = URL(target).openStream()
    val zis = ZipInputStream(download)

    Stream(zis.getNextEntry)
      .map {
        case f if f.getName.matches(".*\\.csv") => Some(io.readInputStream(IO(zis), 4096))
        case _ => None
      }
      .filter(_.isDefined)
      .map(_.get)
      .flatMap(f => f)

  val pipeToFlight: Pipe[IO, String, Option[Flight]] = _
    .map(parseFlightOption)
  val pipeToFlightChunk: Pipe[IO, Chunk[String], Chunk[Option[Flight]]] = _
    .map(_.map(parseFlightOption))

  val filterNone: Pipe[IO, Option[Flight], Flight] = _
    .filter(_.isDefined)
    .map(_.get)
  val filterNoneChunk: Pipe[IO, Chunk[Option[Flight]], Chunk[Flight]] = _
    .map(
      _.filter(
        _.isDefined
      ).map(
        _.get
      )
    )

  val flightSink: Pipe[IO, Flight, Unit] = _.evalMap(insertFlight)
  val flightSinkChunk: Pipe[IO, Chunk[Flight], Unit] = _.evalMapChunk(insertFlight)

  val flightPipeline: Stream[IO, Byte] => Int => Stream[IO, Unit] = source => chunkSize => source
    .through(text.utf8.decode)
    .through(text.lines)
    .chunkN(chunkSize)
    .through(pipeToFlightChunk)
    .through(filterNoneChunk)
    .through(flightSinkChunk)
}
