package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.SharedController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.{sharedModel, sharedCreateModel}
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory

class SharedRoute(SharedController: SharedController) {

  val route: Route = pathPrefix("shared") {
    get {
      path(IntNumber) { id =>
        val result: Future[Either[String, sharedModel]] = SharedController.buscarUsuario(id)
        onSuccess(result) {
          case Right(shared)      => complete(shared)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
        }
      }
    } ~
      path("register") {
        post {
          entity(as[sharedCreateModel]) { shared =>
            val result: Future[Either[String, sharedModel]] =
              SharedController.guardarCompartido(shared.usuario_id, shared.archivo_id)
            onSuccess(result) {
              case Right(newshared)   => complete(StatusCodes.Created, newshared)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }
      } ~
      path("delete") {
        put {
          entity(as[sharedCreateModel]) { id =>
            val result: Future[Either[String, sharedModel]] = SharedController.eliminarCompartido(id)
            onSuccess(result) {
              case Right(newshared)   => complete(StatusCodes.Created, newshared)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }

      }

  }
}
