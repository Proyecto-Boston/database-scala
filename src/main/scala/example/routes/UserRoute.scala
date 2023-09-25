package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.UserController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.{UserModel, UserCreateModel}
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory

class UserRoute(userController: UserController) {

  val route: Route = pathPrefix("user") {
    get {
      path(IntNumber) { id =>
        val result: Future[Either[String, UserModel]] = userController.buscarUsuario(id)
        onSuccess(result) {
          case Right(user)        => complete(user)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
        }
      }
    } ~
      path("register") {
        post {
          entity(as[UserCreateModel]) { user =>
            val result: Future[Either[String, UserModel]] = userController.registrarUsuario(user.nombre, user.apellido)
            onSuccess(result) {
              case Right(newUser)     => complete(StatusCodes.Created, newUser)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }
      } ~
      path("delete") {
        put {
          entity(as[List[Int]]) { ids =>
            val result: Future[List[Either[String, UserModel]]] = userController.eliminarUsuarios(ids)
            onSuccess(result) { resultList =>
              val errors = resultList.collect { case Left(errorMessage) => errorMessage }
              if (errors.isEmpty) {
                val users = resultList.collect { case Right(user) => user }
                complete(StatusCodes.OK, users)
              } else {
                complete(HttpResponse(StatusCodes.InternalServerError, entity = errors.mkString(", ")))

              }
            }
          }
        }

      }
  }
}
