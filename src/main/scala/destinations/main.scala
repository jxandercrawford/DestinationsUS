package destinations

import cats.effect.{ExitCode, IO, IOApp}
import fs2.{Chunk, text}
import destinations.pipeline.pipeline.*
import model.implicits.*
import config.{getConfig, generateDateRange}
import java.time.LocalDate
import java.io.File
import scala.language.postfixOps
import cats.implicits.*

object main extends IOApp {

  private val properties = getConfig match
    case Left(e) => throw e
    case Right(p) => p
  private val projectRoot = new File(".").getCanonicalPath

  /**
   * Create a stream from the BTS website to the lines of the CSV file.
   * @param month A month to target.
   * @param year A year to target.
   * @return A stream of the file CSV lines.
   */
  private def loadTranstats(month: Int, year: Int): fs2.Stream[IO, Chunk[String]] =
    val src = downloadFlightFile(
      properties.getProperty("target_url_base") + s"On_Time_Reporting_Carrier_On_Time_Performance_1987_present_${year}_$month.zip",
      projectRoot + properties.getProperty("data_directory_path") + s"${year}_$month.zip",
      projectRoot + properties.getProperty("data_directory_path") + s"${year}_$month/",
      s"On_Time_Reporting_Carrier_On_Time_Performance_(1987_present)_${year}_$month.csv"
    )

    src
      .through(text.utf8.decode)
      .through(text.lines)
      .chunkN(properties.getProperty("chunk_size").toInt)

  def run(args: List[String]): IO[ExitCode] =

    val start = LocalDate.of(properties.getProperty("year_range_start").toInt, properties.getProperty("month_range_start").toInt, 1)
    val end = LocalDate.of(properties.getProperty("year_range_end").toInt, properties.getProperty("month_range_end").toInt, 1)

    val range = generateDateRange(start, end).toList

    fs2.Stream.emits(range).map(d => loadTranstats(d._1, d._2))
      .parJoin(properties.getProperty("concurrency_degree_max").toInt)
      .through(pipeToFlightChunk)
      .through(filterNoneChunk)
      .through(flightSinkChunk)
      .compile
      .drain
      .flatMap(
        _ => range.map(
          (m, y) => deleteFiles(projectRoot + properties.getProperty("data_directory_path") + s"${y}_$m")
        ).sequence_
      )
      .as(ExitCode.Success)
}
