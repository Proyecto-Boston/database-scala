package routes

import example.DatabaseConnectionManager

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.DirectoryController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.DirectoryModel
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory
import java.sql.Connection
import javax.sql.DataSource
import org.mariadb.jdbc.MariaDbPoolDataSource
import scalikejdbc.ConnectionPool

class DirectoryRoute(directoryController: DirectoryController) {

  Class.forName("org.mariadb.jdbc.Driver")
  val config = ConfigFactory.load()
  val url = DatabaseConnectionManager.dbUrl
  val user = DatabaseConnectionManager.dbUser 
  val password = DatabaseConnectionManager.dbPassword

  ConnectionPool.singleton(url, user, password)  
  case class DirectoryCreateRequest(nombre: String, ruta: String, usuario_id: Int)


  val route: Route = pathPrefix("directory") {
    get {
      path(IntNumber) { id =>
        val result: Future[Either[String, DirectoryModel]] = directoryController.buscarDirectorio(id)
        onSuccess(result) {
          case Right(directory) => complete(directory)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
        }
      }
    } ~
    path("saveDirectory") {
      post {
        entity(as[DirectoryCreateRequest]) { directory =>
          val result: Future[Either[String, DirectoryModel]] = directoryController.guardarDirectorio(directory.nombre, directory.ruta, directory.usuario_id)
          onSuccess(result) {
            case Right(newDirectory) => complete(StatusCodes.Created, newDirectory)
            case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
          }
        }
      }
    }
  }
}
