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

object load {

  private val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:flights",
    "jxan",
    ""
  )

  def insertAirport(airport: Airport): doobie.Update0 =
    sql"INSERT INTO airport (id, name, city, state) VALUES ($airport) ON CONFLICT DO NOTHING;"
      .update

  def insertFlight(flight: Flight): doobie.Update0 =
    sql"INSERT INTO flight (date, origin, destination) VALUES ($flight) ON CONFLICT DO NOTHING;"
      .update

  def insertRecord(flight: Flight): doobie.ConnectionIO[Int] =
    (insertAirport(flight.origin).run, insertAirport(flight.destination).run, insertFlight(flight).run).mapN(_+_+_)

  def insertChunk(flights: Chunk[Flight]): IO[Unit] =
    flights.map(insertRecord).sequence_.transact(xa)
}
