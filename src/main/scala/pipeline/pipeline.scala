package pipeline

import cats.effect.IO
import doobie.ConnectionIO
import doobie.implicits.toConnectionIOOps
import fs2.io.file.{Files, Path}
import fs2.{Pipe, Stream, Chunk, text}
import model.Flight
import model.parser.parseFlightOption
import extract.{deleteFile, downloadFromURL, unzipFile}
import load.{insertChunk}

object pipeline {
  def downloadFlightFile(urlToDownload: String, zipFilePath: String, unzipFilePath: String, targetFileName: String): Stream[IO, Byte] =
    Stream.eval {
      IO {
        downloadFromURL(urlToDownload, zipFilePath)
        unzipFile(zipFilePath, unzipFilePath, "\\S+\\.csv")
        deleteFile(zipFilePath)
      }
    }.flatMap(_ => Files[IO].readAll(Path(unzipFilePath + targetFileName)))

  val pipeToFlight: Pipe[IO, String, Option[Flight]] = _
    .through(text.lines)
    .map(parseFlightOption)

  val filterNone: Pipe[IO, Option[Flight], Flight] = _
    .filter(_.isDefined)
    .map(_.get)

  val flightSink: Pipe[IO, Chunk[Flight], Unit] = _.evalMapChunk(insertChunk)
}
