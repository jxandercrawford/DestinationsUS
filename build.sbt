ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "DestinationsUS"
  )

libraryDependencies ++= Seq(
  "org.scalactic"  %% "scalactic"       % "3.2.16",
  "org.scalatest"  %% "scalatest"       % "3.2.16"     % "test",
  "ch.qos.logback" %  "logback-classic" % "1.4.7",
  "co.fs2"         %% "fs2-core"        % "3.7.0",
  "co.fs2"         %% "fs2-io"          % "3.7.0",
  "org.tpolecat"   %% "doobie-core"     % "1.0.0-RC1",
  "org.tpolecat"   %% "doobie-postgres" % "1.0.0-RC1",
  "org.tpolecat"   %% "doobie-hikari"   % "1.0.0-RC1",
  "net.sf.opencsv" %  "opencsv"         % "2.3"
)