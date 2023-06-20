package destinations

import java.io.File
import java.net.URL
import java.time.LocalDate
import java.util.*
import scala.io.Source

type Month = Int
type Year = Int

object config {

  private val propertiesPath = new File(".").getCanonicalPath + "/resources/application.properties"

  /**
   * Load the application properties file located in this projects `/resources/applications.properties`.
   * @return A properties object containing app settings.
   */
  def getConfig: Either[Throwable, Properties] =
    val properties: Properties = new Properties()

    try
      val source = Source.fromFile(propertiesPath)
      properties.load(source.bufferedReader())
      Right(properties)
    catch
      case e: Throwable => Left(e)

  /**
   * Generate a date range of months between a start and end date.
   * @param start The date to start the range.
   * @param end The date to end the range on.
   * @return A LazyList of tuples containing a `Month` and a `Year` from start to end.
   */
  def generateDateRange(start: LocalDate, end: LocalDate): LazyList[(Month, Year)] =
    val e = end.plusMonths(1)
    LazyList.iterate(start)(_.plusMonths(1))
      .takeWhile(d => d.isBefore(e))
      .map( d => (d.getMonthValue, d.getYear) )
}
