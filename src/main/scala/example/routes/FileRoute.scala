package routes

import example.DatabaseConnectionManager

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.FileController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.FileModel
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory
import java.sql.Connection
import javax.sql.DataSource
import org.mariadb.jdbc.MariaDbPoolDataSource
import scalikejdbc.ConnectionPool

class FileRoute(fileController: FileController) {

  Class.forName("org.mariadb.jdbc.Driver")
  val config = ConfigFactory.load()
  val url = DatabaseConnectionManager.dbUrl
  val user = DatabaseConnectionManager.dbUser 
  val password = DatabaseConnectionManager.dbPassword

  ConnectionPool.singleton(url, user, password)  
  case class FileCreateRequest(nombre: String, ruta: String, tamano: Double, usuario_id: Int)



  val route: Route = pathPrefix("file") {
    get {
      path(IntNumber) { id =>
        val result: Future[Either[String, FileModel]] = fileController.buscarArchivo(id)
        onSuccess(result) {
          case Right(file) => complete(file)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
        }
      }
    } ~
    path("saveFile") {
      post {
        entity(as[FileCreateRequest]) { file =>
          val result: Future[Either[String, FileModel]] = fileController.guardarArchivo(file.nombre, file.ruta, file.tamano, file.usuario_id)
          onSuccess(result) {
            case Right(newFile) => complete(StatusCodes.Created, newFile)
            case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
          }
        }
      }
    }
  }
}
