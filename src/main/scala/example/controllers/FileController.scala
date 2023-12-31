package controllers

import scalikejdbc._
import models.{FileModel, FileReportModel}

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

class FileController {
  implicit val session: DBSession = AutoSession

  def buscarArchivo(id: Int): Future[Either[String, FileModel]] = {
    Future {
      try {
        val archivoOption = sql"SELECT * FROM archivos WHERE id = $id"
          .map { rs =>
            FileModel(
              rs.int("id"),
              rs.string("nombre"),
              rs.string("ruta"),
              rs.double("tamano"),
              rs.int("usuario_id"),
              rs.boolean("habilitado"),
              rs.int("nodo_id"),
              rs.int("directorio_id"),
              rs.int("respaldo_id")
            )
          }
          .single()
          .map { file =>
            // Respuesta exitosa con estado 200 y JSON de usuario
            Right(file)
          }

        archivoOption.getOrElse {
          // Usuario no encontrado con código 404
          Left("Archivo no encontrado")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
          Left("Error interno del servidor")
      }
    }
  }

  def guardarArchivos(
      archivos: List[(String, String, Double, Int, Int, Int, Int)]
  ): Future[List[Either[String, FileModel]]] = {
    Future.sequence {
      archivos.map { case (nombre, ruta, tamano, usuario_id, nodo_id, directorio_id, respaldo_id) =>
        Future {
          try {
            val result =
              sql"INSERT INTO archivos (nombre, ruta, tamano, usuario_id, nodo_id, directorio_id, respaldo_id) VALUES ($nombre, $ruta, $tamano, $usuario_id, $nodo_id,$directorio_id, $respaldo_id)"
                .update()

            if (result > 0) {
              // Recupera el ID generado por la base de datos
              val generatedId: Long = sql"SELECT LAST_INSERT_ID()".map(rs => rs.long(1)).single().getOrElse(0L)

              // Crea una instancia de DirectoryModel con el ID real
              val archivo = FileModel(
                generatedId.toInt,
                nombre,
                ruta,
                tamano,
                usuario_id,
                true,
                nodo_id,
                directorio_id,
                respaldo_id
              )
              Right(archivo)
            } else {
              Left("No se pudo agregar el archivo")
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

  def moverArchivo(id: Int, nuevaRuta: String, directorio_id: Int): Future[Either[String, FileModel]] = {
    Future {
      try {
        // Realizar la actualización en la base de datos
        val resultado =
          sql"UPDATE archivos SET ruta = $nuevaRuta, directorio_id= $directorio_id WHERE id = $id".update()

        if (resultado > 0) {
          // La actualización fue exitosa, llamar a buscarArchivo para obtener el archivo actualizado
          val resultadoFuture: Future[Either[String, FileModel]] = buscarArchivo(id)

          val resultado: Either[String, FileModel] = Await.result(resultadoFuture, 5.seconds)

          resultado match {
            case Right(fileModel) =>
              Right(fileModel)
            case Left(errorMessage) =>
              Left(errorMessage)
          }
        } else {
          // La actualización no afectó ninguna fila, devolver un mensaje de error
          Left("No se pudo mover el archivo, ID no encontrado o la ruta no ha cambiado")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}")
          Left("Error interno del servidor")
      }
    }
  }

  def eliminarArchivo(ids: List[Int]): Future[List[Either[String, FileModel]]] = {
    Future.sequence(ids.map { id =>
      Future {
        try {
          val resultado = sql"UPDATE archivos SET habilitado = false WHERE id = $id".update()

          if (resultado > 0) {
            val resultadoFuture: Future[Either[String, FileModel]] = buscarArchivo(id)

            val resultado: Either[String, FileModel] = Await.result(resultadoFuture, 5.seconds)

            resultado match {
              case Right(fileModel) =>
                println(s"Archivo encontrado: $fileModel")
                Right(fileModel)
              case Left(errorMessage) =>
                println(s"Error: $errorMessage")
                Left(errorMessage)
            }

          } else {
            Left("No se pudo eliminar el archivo, ID no encontrado o ya está deshabilitado")
          }
        } catch {
          case e: Exception =>
            println(s"Error interno del servidor: ${e.getMessage}")
            Left("Error interno del servidor")
        }
      }
    })
  }

  def reporteEspacio(usuario_id: Int): Future[Either[String, FileReportModel]] = {
    Future {
      try {
        val resultado = sql"SELECT usuario_id, SUM(tamano) AS espacio FROM archivos WHERE usuario_id = $usuario_id"
          .map { rs =>
            FileReportModel(rs.int("usuario_id"), rs.double("espacio"))
          }
          .single()
          .map { file =>
            // Respuesta exitosa con estado 200 y JSON de usuario
            Right(file)
          }

        resultado.getOrElse {
          // Usuario no encontrado con código 404
          Left("Error en el reporte")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
          Left("Error interno del servidor")
      }
    }
  }
  def obtenerArchivosPorUsuario(usuario_id: Int): Future[Either[String, List[FileModel]]] = {
    Future {
      try {
        val archivos = sql"SELECT * FROM archivos WHERE habilitado = true AND usuario_id = $usuario_id"
          .map { rs =>
            FileModel(
              rs.int("id"),
              rs.string("nombre"),
              rs.string("ruta"),
              rs.double("tamano"),
              rs.int("usuario_id"),
              rs.boolean("habilitado"),
              rs.int("nodo_id"),
              rs.int("directorio_id"),
              rs.int("respaldo_id")
            )
          }
          .list()

        // Respuesta exitosa con estado 200 y lista de archivos
        Right(archivos)
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
          Left("Error interno del servidor")
      }
    }
  }

  def obtenerArchivosPorDirectorio(directorio_id: Int): Future[Either[String, List[FileModel]]] = {
    Future {
      try {
        val archivos = sql"SELECT * FROM archivos WHERE habilitado = true AND directorio_id = $directorio_id"
          .map { rs =>
            FileModel(
              rs.int("id"),
              rs.string("nombre"),
              rs.string("ruta"),
              rs.double("tamano"),
              rs.int("usuario_id"),
              rs.boolean("habilitado"),
              rs.int("nodo_id"),
              rs.int("directorio_id"),
              rs.int("respaldo_id")
            )
          }
          .list()

        // Respuesta exitosa con estado 200 y lista de archivos
        Right(archivos)
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
          Left("Error interno del servidor")
      }
    }
  }

  def renombrarArchivo(id: Int, nuevoNombre: String): Future[Either[String, String]] = {
    Future {
      try {
        // Realizar la actualización en la base de datos
        val resultado =
          sql"UPDATE archivos SET nombre = $nuevoNombre WHERE id = $id".update()

        if (resultado > 0) {
          Right("Renombre exitoso")

        } else {
          Left("No se pudo renombrar el archivo")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}")
          Left("Error interno del servidor")
      }
    }
  }
}
