package destinations.model

import au.com.bytecode.opencsv.CSVReader
import destinations.model.implicits.*
import java.io.StringReader

object parser {

  private val rowReader: String => CSVReader = rawTuple => CSVReader(StringReader(rawTuple))

  private val ORIGIN_AIRPORT_MAPPING: Map[Int, String] = Map(
    11 -> "id",
    14 -> "name",
    15 -> "city",
    16 -> "state"
  )

  private val DESTINATION_AIRPORT_MAPPING: Map[Int, String] = Map(
    20 -> "id",
    23 -> "name",
    24 -> "city",
    25 -> "state"
  )

  private val FLIGHT_MAPPING: Map[Int, String] = Map(
    5 -> "date",
    54 -> "distance",
    52 -> "airtime",
    31 -> "departDelay",
    36 -> "taxiOut",
    42 -> "arriveDelay",
    39 -> "taxiIn"
  )

  private def parseSeq[A](mapping: Map[Int, String])(x: Seq[A]): Map[String, A] =
    for
      (k, v) <- mapping
      d <- x.lift(k)
    yield v -> d

  def parseFlight(rawTuple: String): Flight =
    val row = rowReader(rawTuple)
    val raw = row.readNext()
    row.close()

    val originValues = parseSeq(ORIGIN_AIRPORT_MAPPING)(raw)
    val destinationValues = parseSeq(DESTINATION_AIRPORT_MAPPING)(raw)
    val flightValues = parseSeq(FLIGHT_MAPPING)(raw)

    Flight.fromMap(
      flightValues,
      Airport.fromMap(originValues),
      Airport.fromMap(destinationValues)
    )

  def parseFlightOption(rawTuple: String): Option[Flight] =
    try
      Some(parseFlight(rawTuple))
    catch
      case _: Throwable => None
}
