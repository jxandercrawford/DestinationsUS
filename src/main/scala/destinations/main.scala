package destinations

import cats.effect.{ExitCode, IO, IOApp}
import fs2.text
import destinations.pipeline.pipeline.*
import config.getConfig

import java.io.File
import scala.language.postfixOps

object main extends IOApp {

  private val properties = getConfig match
    case Left(e) => throw e
    case Right(p) => p
  private val projectRoot = new File(".").getCanonicalPath

  private def loadTranstats(month: Int, year: Int): fs2.Stream[IO, Unit] =
    val src = downloadFlightFile(
      properties.getProperty("target_url_base") + s"On_Time_Reporting_Carrier_On_Time_Performance_1987_present_${year}_$month.zip",
      projectRoot + properties.getProperty("data_directory_path") + s"${year}_$month.zip",
      projectRoot + properties.getProperty("data_directory_path") + s"${year}_$month/",
      s"On_Time_Reporting_Carrier_On_Time_Performance_(1987_present)_${year}_$month.csv"
    )

    src
      .through(text.utf8.decode)
      .through(pipeToFlightChunk(properties.getProperty("chunk_size").toInt))
      .through(filterNoneChunk)
      .through(flightSinkChunk)

  def run(args: List[String]): IO[ExitCode] =
    val month = properties.getProperty("month").toInt
    val year = properties.getProperty("year").toInt

    loadTranstats(month, year)
      .compile
      .drain
      .flatMap(u => deleteFiles(projectRoot + properties.getProperty("data_directory_path") + s"${year}_$month/"))
      .as(ExitCode.Success)
}
