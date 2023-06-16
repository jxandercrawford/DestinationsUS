import cats.effect.{ExitCode, IO, IOApp}
import fs2.text
import pipeline.pipeline.{downloadFlightFile, filterNone, flightSink, pipeToFlight}

import java.io.File
import scala.language.postfixOps

object main extends IOApp {

  val year = 2020
  val month = 2
  val urlBase = "https://transtats.bts.gov/PREZIP/"
  val urlFile = s"On_Time_Reporting_Carrier_On_Time_Performance_1987_present_${year}_$month.zip"
  val outBase = new File(".").getCanonicalPath + "/resources/data/"
  val outZip = "files.zip"
  val outFile = "outs/"
  val target = s"On_Time_Reporting_Carrier_On_Time_Performance_(1987_present)_${year}_$month.csv"

  val chunkSize = 1000

  def run(args: List[String]): IO[ExitCode] =

    val src = downloadFlightFile(urlBase + urlFile, outBase + outZip, outBase + outFile, target)
    val pipe = src
      .through(text.utf8.decode)
      .through(pipeToFlight)
      .through(filterNone)
      .chunkN(chunkSize)
      .through(flightSink)

    pipe.compile.drain.as(ExitCode.Success)
}
