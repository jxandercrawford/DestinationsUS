package destinations.model

import java.time.LocalDate
import implicits.*

// TODO: Fix the fromMap to include airport parsing.
case class Flight(
                   date: LocalDate,
                   origin: Airport,
                   destination: Airport,
                   distance: Int,
                   airtime: Int,
                   departDelay: Int,
                   taxiOut: Int,
                   arriveDelay: Int,
                   taxiIn: Int
                 )

object Flight {

  def fromMap[A](m: Map[String, A], origin: Airport, destination: Airport): Flight =
    Flight(
      m("date").toString.toDate("yyyy-MM-dd"),
      origin,
      destination,
      m("distance").toString.toDouble.toInt,
      m("airtime").toString.toDouble.toInt,
      m("departDelay").toString.toDouble.toInt,
      m("taxiOut").toString.toDouble.toInt,
      m("arriveDelay").toString.toDouble.toInt,
      m("taxiIn").toString.toDouble.toInt
    )

  def fromMapOption[A](m: Map[String, A], origin: Airport, destination: Airport): Option[Flight] =
    try
      Some(fromMap(m, origin, destination))
    catch
      case _: Throwable => None
}
