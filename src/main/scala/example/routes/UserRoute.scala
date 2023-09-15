package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.UserController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.UserModel
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory

class UserRoute(userController: UserController) {

  case class UserCreateRequest(nombre: String, apellido: String)

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
    path("register") {
      post {
        entity(as[UserCreateRequest]) { user =>
          val result: Future[Either[String, UserModel]] = userController.registrarUsuario(user.nombre, user.apellido)
          onSuccess(result) {
            case Right(newUser) => complete(StatusCodes.Created, newUser)
            case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
          }
        }
      }
    }
  }
}
