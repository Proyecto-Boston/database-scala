import Dependencies._ 

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example" 
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "database-scala",
    
    libraryDependencies ++= Seq(
      scalaTest % Test, "org.scalikejdbc" %% "scalikejdbc" % "4.0.+" exclude("org.scala-lang.modules", "scala-parser-combinators_2.12"),
      "com.h2database" % "h2" % "1.4.+",
      "ch.qos.logback" % "logback-classic" % "1.2.+",
      "org.mariadb.jdbc" % "mariadb-java-client" % "2.7.4",
      "com.typesafe" % "config" % "1.4.1",
      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1",
      // Dependencias de Akka HTTP
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.14", // Para actores tipados
      "com.typesafe.akka" %% "akka-http" % "10.2.4", // Para Akka HTTP
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.4", // Para manejo de JSON
      "com.typesafe.akka" %% "akka-stream" % "2.6.14", // Para manejo de streams
      "de.heikoseeberger" %% "akka-http-circe" % "1.37.0",
    )
  )
