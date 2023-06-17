package pipeline

import cats.*
import cats.data.*
import cats.effect.*
import cats.free.Free
import cats.implicits.*
import doobie.{Update0, ConnectionIO, Transactor}
import doobie.free.connection
import doobie.implicits.*
import fs2.Chunk
import model.implicits.{given_Write_Airport, given_Write_Flight}
import model.{Airport, Flight}

// TODO: Load database details into properties or as a secret

object load {

  private val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:flights",
    "jxan",
    ""
  )

  def createAirportInsert(airport: Airport): doobie.Update0 =
    sql"INSERT INTO airport (id, name, city, state) VALUES ($airport) ON CONFLICT DO NOTHING;"
      .update

  def createFlightInsert(flight: Flight): doobie.Update0 =
    sql"INSERT INTO flight (date, origin, destination) VALUES ($flight) ON CONFLICT DO NOTHING;"
      .update

  def createRecordInsert(flight: Flight): doobie.ConnectionIO[Int] =
    (createAirportInsert(flight.origin).run, createAirportInsert(flight.destination).run, createFlightInsert(flight).run).mapN(_+_+_)

  def insertFlight(flight: Flight): IO[Unit] =
    createRecordInsert(flight).void.transact(xa)
  def insertFlight(flights: Chunk[Flight]): IO[Unit] =
    flights.map(createRecordInsert).sequence_.transact(xa)
}
