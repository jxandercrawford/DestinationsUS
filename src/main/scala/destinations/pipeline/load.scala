package destinations.pipeline

import cats.effect.IO
import cats.implicits.*
import doobie.implicits.*
import doobie.*
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

  private def createAirportInsert(airport: Airport): ConnectionIO[Int] =
    sql"INSERT INTO airport (id, name, city, state) VALUES ($airport) ON CONFLICT DO NOTHING;"
      .update.run

  private def createAirportInsert(airport: Seq[Airport]): ConnectionIO[Int] =
    val insertStatement: String = "INSERT INTO airport (id, name, city, state) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING;"
    Update[Airport](insertStatement).updateMany(airport)

  private def createFlightInsert(flight: Flight): ConnectionIO[Int] =
    sql"INSERT INTO flight (date, origin, destination) VALUES ($flight) ON CONFLICT DO NOTHING;"
      .update.run

  private def createFlightInsert(flight: Seq[Flight]): ConnectionIO[Int] =
    val insertStatement: String = "INSERT INTO flight (date, origin, destination) VALUES (?, ?, ?) ON CONFLICT DO NOTHING;"
    Update[Flight](insertStatement).updateMany(flight)

  private def createRecordInsert(flight: Flight): ConnectionIO[Int] =
    (createAirportInsert(flight.origin), createAirportInsert(flight.destination), createFlightInsert(flight)).mapN(_+_+_)

  def insertFlight(flight: Flight): IO[Unit] =
    createRecordInsert(flight).void.transact(xa)
  def insertFlight(flights: Chunk[Flight]): IO[Unit] =
    (
      createAirportInsert(flights.map(a => a.origin).toList),
      createAirportInsert(flights.map(a => a.destination).toList),
      createFlightInsert(flights.toList)
    ).mapN(_+_+_).void.transact(xa)
}
