package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.FileController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.{FileModel, FileReportModel, FileMoveModel, FileCreateModel, FileRenameModel}
import scala.concurrent.Future

class FileRoute(fileController: FileController) {

  val route: Route = pathPrefix("file") {
    get {
      path(IntNumber) { id =>
        val result: Future[Either[String, FileModel]] = fileController.buscarArchivo(id)
        onSuccess(result) {
          case Right(file)        => complete(file)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
        }
      }
    } ~
      path("save") {
        post {
          entity(as[List[FileCreateModel]]) { files =>
            val result: Future[List[Either[String, FileModel]]] =
              fileController.guardarArchivos(
                files.map(file =>
                  (
                    file.nombre,
                    file.ruta,
                    file.tamano,
                    file.usuario_id,
                    file.nodo_id,
                    file.directorio_id,
                    file.respaldo_id
                  )
                )
              )
            onSuccess(result) { list =>
              val errors = list.collect { case Left(errorMessage) => errorMessage }
              if (errors.isEmpty) {
                val newFiles = list.collect { case Right(file) => file }
                complete(StatusCodes.Created, newFiles)
              } else {
                complete(HttpResponse(StatusCodes.InternalServerError, entity = errors.mkString(", ")))
              }
            }
          }
        }
      } ~
      path("move") {
        put {
          entity(as[FileMoveModel]) { file =>
            val result: Future[Either[String, FileModel]] =
              fileController.moverArchivo(file.id, file.nuevaRuta, file.directorio_id)
            onSuccess(result) {
              case Right(newFile)     => complete(StatusCodes.Created, newFile)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }
      } ~ path("delete") {
        put {
          entity(as[List[Int]]) { ids =>
            val result: Future[List[Either[String, FileModel]]] = fileController.eliminarArchivo(ids)
            onSuccess(result) { resultList =>
              val errors = resultList.collect { case Left(errorMessage) => errorMessage }
              if (errors.isEmpty) {
                val files = resultList.collect { case Right(file) => file }
                complete(StatusCodes.OK, files)
              } else {
                complete(HttpResponse(StatusCodes.InternalServerError, entity = errors.mkString(", ")))
              }
            }
          }
        }
      } ~
      path("report" / IntNumber) { id =>
        get {
          val result: Future[Either[String, FileReportModel]] = fileController.reporteEspacio(id)
          onSuccess(result) {
            case Right(file)        => complete(file)
            case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
          }
        }
      } ~
      path("user" / IntNumber) { id =>
        get {
          val result: Future[Either[String, List[FileModel]]] = fileController.obtenerArchivosPorUsuario(id)
          onSuccess(result) {
            case Right(file)        => complete(file)
            case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))

          }

        }
      } ~
      path("fileByDirectory" / IntNumber) { directorio_id =>
        get {
          val result: Future[Either[String, List[FileModel]]] =
            fileController.obtenerArchivosPorDirectorio(directorio_id)
          onSuccess(result) {
            case Right(file)        => complete(file)
            case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))

          }

        }
      } ~ path("rename") {
        put {
          entity(as[FileRenameModel]) { file =>
            val result: Future[Either[String, String]] =
              fileController.renombrarArchivo(file.id, file.nuevoNombre)
            onSuccess(result) {
              case Right(newFile)     => complete(StatusCodes.Created, newFile)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }
      }
  }
}
