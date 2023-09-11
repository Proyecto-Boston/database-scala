package example

import java.sql.Connection
import javax.sql.DataSource
import org.mariadb.jdbc.MariaDbPoolDataSource
import com.typesafe.config.ConfigFactory

object DatabaseConnectionManager {

  val config = ConfigFactory.load()
  
  val dbUrl = config.getString("db.url")
  val dbUser = config.getString("db.user")
  val dbPassword = config.getString("db.password")


  private val pool: DataSource = new MariaDbPoolDataSource()
  pool.asInstanceOf[MariaDbPoolDataSource].setUrl(dbUrl)
  pool.asInstanceOf[MariaDbPoolDataSource].setUser(dbUser)
  pool.asInstanceOf[MariaDbPoolDataSource].setPassword(dbPassword)

  def getConnection: Connection = pool.getConnection
}
