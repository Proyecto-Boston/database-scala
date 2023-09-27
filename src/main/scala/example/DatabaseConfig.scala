package example

import java.sql.Connection
import java.sql.DriverManager
import com.typesafe.config.ConfigFactory

import scalikejdbc.{ConnectionPool}

object DatabaseConfig {

  val config = ConfigFactory.load()
  val dbMasterUrl = config.getString("db.urlMaestro")
  val dbUser = config.getString("db.user")
  val dbPassword = config.getString("db.password")

  // Método para verificar si el maestro está disponible
  def pingMaestro(): Boolean = {
    try {
      // Intenta establecer una conexión al maestro
      ConnectionPool.singleton(dbMasterUrl, dbUser, dbPassword)
      true
    } catch {
      case _: Exception => false

    }
  }

}
