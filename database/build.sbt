import Dependencies._ 

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example" 
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "database",
    
    libraryDependencies ++= Seq(
      scalaTest % Test, "org.scalikejdbc" %% "scalikejdbc" % "4.0.+",
      "com.h2database" % "h2" % "1.4.+",
      "ch.qos.logback" % "logback-classic" % "1.2.+",
      "org.mariadb.jdbc" % "mariadb-java-client" % "2.7.4",
      "com.typesafe" % "config" % "1.4.1",
      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1"
    )
  )