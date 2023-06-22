package destinations.pipeline

import cats.effect.IO
import destinations.model.Flight
import destinations.model.parser.parseFlightOption
import destinations.pipeline.extract.{deleteFile, downloadFromURL, unzipFile}
import destinations.pipeline.load.insertFlight
import fs2.io.file.{Files, Path}
import fs2.{Chunk, Pipe, Stream, text}

object pipeline {
  def downloadFlightFile(urlToDownload: String, zipFilePath: String, unzipFilePath: String, targetFileName: String): Stream[IO, Byte] =
    Stream.eval {
      IO.cede *> IO.blocking {
        downloadFromURL(urlToDownload, zipFilePath)
        unzipFile(zipFilePath, unzipFilePath, "\\S+\\.csv")
        deleteFile(zipFilePath)
      }.guarantee(IO.cede)
    }.flatMap(_ => Files[IO].readAll(Path(unzipFilePath + targetFileName)))

  val deleteFiles: String => IO[Unit] = filePath =>
    IO.cede *> IO.blocking {
      deleteFile(filePath)
    }.guarantee(IO.cede)

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
}
