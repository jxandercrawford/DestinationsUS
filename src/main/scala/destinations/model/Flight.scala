package destinations.model

import java.time.LocalDate

case class Flight(date: LocalDate, origin: Airport, destination: Airport)
