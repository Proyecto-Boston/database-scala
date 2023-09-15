package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.FileController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.{FileModel, FileReportModel, FileMoveModel,FileCreateModel}
import scala.concurrent.Future

class FileRoute(fileController: FileController) {

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
    path("save") {
      post {
        entity(as[FileCreateModel]) { file =>
          val result: Future[Either[String, FileModel]] = fileController.guardarArchivo(file.nombre, file.ruta, file.tamano, file.usuario_id)
          onSuccess(result) {
            case Right(newFile) => complete(StatusCodes.Created, newFile)
            case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
          }
        }
      }
    } ~
    path("move") {
      put  {
        entity(as[FileMoveModel]) { file =>
          val result: Future[Either[String, FileModel]] = fileController.moverArchivo(file.id, file.nuevaRuta)
          onSuccess(result) {
            case Right(newFile) => complete(StatusCodes.Created, newFile)
            case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
          }
        }
      } 
    } ~
    path("delete" / IntNumber) { id =>
      put {
        val result: Future[Either[String, FileModel]] = fileController.eliminarArchivo(id)
        onSuccess(result) {
          case Right(deletedFile) => complete(StatusCodes.OK, deletedFile)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
        }
      }
    } ~
    path("report" / IntNumber) { id =>
        get {
        val result: Future[Either[String, FileReportModel]] = fileController.reporteEspacio(id)
        onSuccess(result) {
          case Right(file) => complete(file)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
        }
      }
    } ~
    path("user" / IntNumber) { id =>
      get {
        val result: Future[Either[String, List[FileModel]]] = fileController.obtenerArchivosPorUsuario(id)
        onSuccess(result) {
          case Right(file) => complete(file)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))

      }
    
  
  }
}
}
}