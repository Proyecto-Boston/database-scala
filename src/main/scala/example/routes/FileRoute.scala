package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.FileController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.FileModel
import scala.concurrent.Future

class FileRoute(fileController: FileController) {

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
