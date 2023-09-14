package routes

import example.DatabaseConnectionManager

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.UserController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.UserModel
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory
import java.sql.Connection
import javax.sql.DataSource
import org.mariadb.jdbc.MariaDbPoolDataSource
import scalikejdbc.ConnectionPool





class UserRoute(userController: UserController) {

  Class.forName("org.mariadb.jdbc.Driver")
  val config = ConfigFactory.load()
  val url = DatabaseConnectionManager.dbUrl
  val user = DatabaseConnectionManager.dbUser 
  val password = DatabaseConnectionManager.dbPassword

  ConnectionPool.singleton(url, user, password)  

  val route: Route = pathPrefix("users") {
    get {
      path(IntNumber) { id =>
        val result: Future[Either[String, UserModel]] = userController.buscarUsuario(id)
        onSuccess(result) {
          case Right(user) => complete(user)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
        }
      }
    } ~
    post {
      entity(as[UserModel]) { user =>
        val result: Future[Either[String, UserModel]] = userController.registrarUsuario(user.nombre, user.apellido)
        onSuccess(result) {
          case Right(newUser) => complete(StatusCodes.Created, newUser)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
        }
      }
    }
  }
}
