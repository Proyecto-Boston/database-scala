package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.SharedController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.{SharedModel, SharedCreateModel, SharedDeleteModel, FileModelShared}
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory
import scala.concurrent.Future

class SharedRoute(sharedController: SharedController) {

  val route: Route = pathPrefix("shared") {

    get {
      path(IntNumber) { id =>
        val results: Future[Either[String, List[FileModelShared]]] = sharedController.obtenerCompartidosPorUsuario(id)
        onSuccess(results) {
          case Right(files)       => complete(files)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
        }
      }
    } ~ path("register") {
      post {
        entity(as[SharedCreateModel]) { shared =>
          val result: Future[Either[String, SharedModel]] =
            sharedController.guardarCompartido(shared.usuario_id, shared.archivo_id)
          onSuccess(result) {
            case Right(newshared)   => complete(StatusCodes.Created, newshared)
            case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
          }
        }
      }
    } ~
      path("delete") {
        post {
          entity(as[SharedDeleteModel]) { shared =>
            val result: Future[Either[String, String]] = sharedController.eliminarCompartido(shared.id)
            onSuccess(result) {
              case Right(newshared)   => complete(StatusCodes.Created, newshared)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }

      }

  }
}
