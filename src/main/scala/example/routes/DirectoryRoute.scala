package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.DirectoryController
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import models.{DirectoryModel, DirectoryCreateModel, SubDirectoryCreateModel, Directorysearch}
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory

class DirectoryRoute(directoryController: DirectoryController) {

  val route: Route = pathPrefix("directory") {
    get {
      path(IntNumber) { id =>
        val result: Future[Either[String, DirectoryModel]] = directoryController.buscarDirectorio(id)
        onSuccess(result) {
          case Right(directory)   => complete(directory)
          case Left(errorMessage) => complete(HttpResponse(StatusCodes.NotFound, entity = errorMessage))
        }
      }
    } ~
      path("saveDirectories") {
        post {
          entity(as[List[DirectoryCreateModel]]) { directories =>
            val results: Future[List[Either[String, DirectoryModel]]] = directoryController.guardarDirectorios(
              directories.map(directory =>
                (directory.nombre, directory.ruta, directory.usuario_id, directory.nodo_id, directory.respaldo_id)
              )
            )
            onSuccess(results) { list =>
              val errors = list.collect { case Left(errorMessage) => errorMessage }
              if (errors.isEmpty) {
                val newDirectories = list.collect { case Right(directory) => directory }
                complete(StatusCodes.Created, newDirectories)
              } else {
                complete(HttpResponse(StatusCodes.InternalServerError, entity = errors.mkString(", ")))
              }
            }
          }
        }
      } ~
      path("saveSubDirectories") {
        post {
          entity(as[List[SubDirectoryCreateModel]]) { subDirectories =>
            val results: Future[List[Either[String, DirectoryModel]]] = directoryController.guardarSubDirectorios(
              subDirectories.map(subDirectory =>
                (
                  subDirectory.nombre,
                  subDirectory.ruta,
                  subDirectory.usuario_id,
                  subDirectory.nodo_id,
                  subDirectory.padre_id,
                  subDirectory.respaldo_id
                )
              )
            )
            onSuccess(results) { list =>
              val errors = list.collect { case Left(errorMessage) => errorMessage }
              if (errors.isEmpty) {
                val newSubDirectories = list.collect { case Right(subDirectory) => subDirectory }
                complete(StatusCodes.Created, newSubDirectories)
              } else {
                complete(HttpResponse(StatusCodes.InternalServerError, entity = errors.mkString(", ")))
              }
            }
          }
        }
      } ~
      path("delete") {
        put {
          entity(as[Int]) { id =>
            val result: Future[Either[String, String]] = directoryController.borrarDirectorio(id)
            onSuccess(result) {
              case Right(newshared)   => complete(StatusCodes.Created, newshared)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }

      } ~
      path("buscarSubDirectorio") {
        post {
          entity(as[Int]) { padre_id =>
            val result: Future[Either[String, List[DirectoryModel]]] =
              directoryController.buscarSubDirectorio(padre_id)
            onSuccess(result) {
              case Right(newshared)   => complete(StatusCodes.Created, newshared)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }

      } ~
      path("buscarDirectorioRoot") {
        post {
          entity(as[Int]) { usuario_id =>
            val result: Future[Either[String, List[DirectoryModel]]] =
              directoryController.buscarDirectorioRoot(usuario_id)
            onSuccess(result) {
              case Right(newshared)   => complete(StatusCodes.Created, newshared)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }
      } ~ path("deshabilitarSubdirectorios") {
        put {
          entity(as[Int]) { directoryId =>
            val result: Future[Either[String, String]] =
              directoryController.deshabilitarSubdirectorios(directoryId)
            onSuccess(result) {
              case Right(message) => complete(StatusCodes.OK, message)
              case Left(errorMessage) =>
                complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }

      } ~ path("deshabilitarArchivos") {
        put {
          entity(as[Int]) { directoryId =>
            val result: Future[Either[String, String]] =
              directoryController.deshabilitarArchivos(directoryId)
            onSuccess(result) {
              case Right(message)     => complete(StatusCodes.OK, message)
              case Left(errorMessage) => complete(HttpResponse(StatusCodes.InternalServerError, entity = errorMessage))
            }
          }
        }
      }

  }
}
