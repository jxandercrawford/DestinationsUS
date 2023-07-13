package destinations.model

import doobie.Write
import doobie.postgres.implicits.*

import java.time.{LocalDate, LocalDateTime}
import java.sql.Timestamp
import java.time.format.DateTimeFormatter

// TODO: Remove implicits and move towards scala3's givens

object implicits {

  implicit class DateUtils(s: String) {

    def toDate(format: String): LocalDate = LocalDate.parse(s, DateTimeFormatter.ofPattern(format))

    def toDateOption(format: String): Option[LocalDate] = try
      Some(
        s.toDate(format)
      )
    catch
      case _: Throwable => None

    def toDatetime(format: String): LocalDateTime = LocalDateTime.parse(s, DateTimeFormatter.ofPattern(format))

    def toDatetimeOption(format: String): Option[LocalDateTime] = try
      Some(
        s.toDatetime(format)
      )
    catch
      case _: Throwable => None
  }

  given Write[Airport] = Write[(Int, String, String, String)].contramap(c => (c.id, c.name, c.city, c.state))
  given Write[Flight] = Write[(LocalDate, Int, Int, Int, Int, Int, Int, Int, Int)]
    .contramap(c =>
      (c.date, c.origin.id, c.destination.id, c.distance, c.airtime, c.departDelay, c.taxiOut, c.arriveDelay, c.taxiIn)
    )
}
