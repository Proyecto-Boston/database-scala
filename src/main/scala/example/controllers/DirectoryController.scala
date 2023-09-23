package controllers

import scalikejdbc._
import models.DirectoryModel
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DirectoryController {
  implicit val session: DBSession = AutoSession

  def buscarDirectorio(id: Int): Future[Either[String, DirectoryModel]] = {
    Future {
      try {
        val directorioOption = sql"SELECT * FROM directorios WHERE id = $id"
          .map { rs =>
            DirectoryModel(rs.int("id"), rs.string("nombre"), rs.string("ruta"), rs.int("usuario_id"))
          }
          .single()
          .map { directorio =>
            // Respuesta exitosa con estado 200 y JSON de usuario
            Right(directorio)
          }

        directorioOption.getOrElse {
          // Usuario no encontrado con código 404
          Left("Directorio no encontrado")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
          Left("Error interno del servidor")
      }
    }
  }

  def guardarDirectorios(directorios: List[(String, String, Int)]): Future[List[Either[String, DirectoryModel]]] = {
    Future.sequence {
      directorios.map { case (nombre, ruta, usuario_id) =>
        Future {
          try {
            val result = sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $ruta, $usuario_id)"
              .update()

            if (result > 0) {
              // Recupera el ID generado por la base de datos
              val generatedId: Long = sql"SELECT LAST_INSERT_ID()".map(rs => rs.long(1)).single().getOrElse(0L)

              // Crea una instancia de DirectoryModel con el ID real
              val directorio = DirectoryModel(generatedId.toInt, nombre, ruta, usuario_id)
              Right(directorio)
            } else {
              Left("No se pudo agregar el directorio")
            }
          } catch {
            case e: Exception =>
              println(s"Error interno del servidor: ${e.getMessage}")
              Left("Error interno del servidor")
          }
        }
      }
    }
  }

  def guardarSubDirectorios(
      subdirectorios: List[(String, String, Int)]
  ): Future[List[Either[String, DirectoryModel]]] = {
    Future.sequence {
      subdirectorios.map { case (nombre, rutaPadre, usuario_id) =>
        Future {
          try {
            val nuevaRuta = s"$rutaPadre/$nombre"

            val result =
              sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $nuevaRuta, $usuario_id)"
                .update()

            if (result > 0) {
              // Recupera el ID generado por la base de datos
              val generatedId: Long = sql"SELECT LAST_INSERT_ID()".map(rs => rs.long(1)).single().getOrElse(0L)

              // Crea una instancia de DirectoryModel con el ID real
              val directorio = DirectoryModel(generatedId.toInt, nombre, nuevaRuta, usuario_id)
              Right(directorio)
            } else {
              Left("No se pudo agregar el sub directorio")
            }
          } catch {
            case e: Exception =>
              println(s"Error interno del servidor: ${e.getMessage}")
              Left("Error interno del servidor")
          }
        }
      }
    }
  }

  def deshabilitarSubdirectorios(idDirectorio: Int): Future[Either[String, String]] = {
    Future {
      try {
        // Deshabilita todos los subdirectorios del directorio especificado
        val disableResult = sql"UPDATE directorios SET habilitado = false WHERE id_padre = $idDirectorio"
          .update()

        if (disableResult > 0) {
          // Subdirectorios deshabilitados correctamente
          Right("Subdirectorios deshabilitados correctamente")
        } else {
          // No se encontraron subdirectorios o no se pudieron deshabilitar
          Left("No se pudieron deshabilitar los subdirectorios")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}")
          Left("Error interno del servidor")
      }
    }
  }

  def deshabilitarArchivos(idDirectorio: Int): Future[Either[String, String]] = {
    Future {
      try {
        // Deshabilita todos los archivos del directorio especificado
        val disableResult = sql"UPDATE archivos SET habilitado = false WHERE directorio_id = $idDirectorio"
          .update()

        if (disableResult > 0) {
          // Archivos deshabilitados correctamente
          Right("Archivos deshabilitados correctamente")
        } else {
          // No se encontraron archivos o no se pudieron deshabilitar
          Left("No se pudieron deshabilitar los archivos")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}")
          Left("Error interno del servidor")
      }
    }
  }
  /*
  def borrarDirectorio(id: Int): Future[Either[String, String]] = {
    Future {
      try {
        // Deshabilita todos los subdirectorios del directorio especificado
        deshabilitarSubdirectorios(id).flatMap {
          case Right(_) =>
            // Deshabilita todos los archivos del directorio especificado
            deshabilitarArchivos(id).flatMap {
              case Right(_) =>
                // Deshabilita el directorio
                val disableResult = sql"UPDATE directorios SET habilitado = false WHERE id = $id"
                  .update()

                if (disableResult > 0) {
                  // Directorio y su contenido deshabilitados correctamente
                  Right("Directorio y su contenido deshabilitados correctamente")
                } else {
                  // No se encontró el directorio o no se pudo deshabilitar
                  Left("No se pudo deshabilitar el directorio y su contenido")
                }
              case Left(error) => Future(Left(error))
            }
          case Left(error) => Future(Left(error))
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}")
          Left("Error interno del servidor")
      }
    }
  }
   */

}
