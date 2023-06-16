package pipeline

import cats.*
import cats.data.*
import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor.*
import model.implicits.{given_Write_Airport, given_Write_Flight}
import model.{Airport, Flight}

object load {

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:flights",
    "jxan",
    ""
  )

  def insertAirport(airport: Airport): IO[Unit] =
    sql"INSERT INTO airport (id, name, city, state) VALUES ($airport) ON CONFLICT DO NOTHING;"
      .update.run.void.transact(xa)

  def insertFlight(flight: Flight): IO[Unit] =
    sql"INSERT INTO flight (date, origin, destination) VALUES ($flight) ON CONFLICT DO NOTHING;"
      .update.run.void.transact(xa)

  def insertRecord(flight: Flight): IO[Unit] =
    for
      _ <- insertAirport(flight.origin)
      _ <- insertAirport(flight.destination)
      _ <- insertFlight(flight)
    yield ()
}
