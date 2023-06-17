package destinations.pipeline

import cats.effect.IO
import cats.implicits.*
import doobie.implicits.*
import doobie.{ConnectionIO, Transactor, Update0}
import fs2.Chunk

import destinations.model.{Flight, Airport}
import destinations.model.implicits.{given_Write_Flight, given_Write_Airport}
import destinations.config.getConfig

// TODO: Load database details into properties or as a secret

object load {

  private val properties = getConfig match
    case Left(e) => throw e
    case Right(p) => p

  private val xa = Transactor.fromDriverManager[IO](
    properties.getProperty("database_driver"),
    properties.getProperty("database_url"),
    properties.getProperty("database_user"),
    ""
  )

  private def createAirportInsert(airport: Airport): Update0 =
    sql"INSERT INTO airport (id, name, city, state) VALUES ($airport) ON CONFLICT DO NOTHING;"
      .update

  private def createFlightInsert(flight: Flight): Update0 =
    sql"INSERT INTO flight (date, origin, destination) VALUES ($flight) ON CONFLICT DO NOTHING;"
      .update

  private def createRecordInsert(flight: Flight): ConnectionIO[Int] =
    (createAirportInsert(flight.origin).run, createAirportInsert(flight.destination).run, createFlightInsert(flight).run).mapN(_+_+_)

  def insertFlight(flight: Flight): IO[Unit] =
    createRecordInsert(flight).void.transact(xa)
  def insertFlight(flights: Chunk[Flight]): IO[Unit] =
    flights.map(createRecordInsert).sequence_.transact(xa)
}
