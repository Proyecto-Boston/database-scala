package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.DirectoryController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.{DirectoryModel, DirectoryCreateModel}
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory


class DirectoryRoute(directoryController: DirectoryController) {

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
        entity(as[DirectoryCreateModel]) { directory =>
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
