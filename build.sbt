ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "Rule_engine"
  )
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.33"
libraryDependencies += "com.typesafe" % "config" % "1.4.3"
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4" cross CrossVersion.for3Use2_13