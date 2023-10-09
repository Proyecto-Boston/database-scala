//val resultado = sql"INSERT INTO compartidos (archivo_id, usuario_id) VALUES ($idArchivo, $idUsuario)"
package controllers

import scalikejdbc._
import models.{SharedModel}

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

class SharedController {
  implicit val session: DBSession = AutoSession

  def guardarCompartido(
      archivos: List[( Int, Int, Int)]
  ): Future[List[Either[String, SharedModel]]] = {
    Future.sequence {
      archivos.map { case (usuario_id, archivo_id) =>
        Future {
          try {
            val result =
              sql"INSERT INTO compartidos (usuario_id, archivo_id) VALUES ($usuario_id, $archivo_id)"
                .update()

            if (result > 0) {
              // Recupera el ID generado por la base de datos
              val generatedId: Long = sql"SELECT LAST_INSERT_ID()".map(rs => rs.long(1)).single().getOrElse(0L)

              // Crea una instancia de DirectoryModel con el ID real
              val comparitdo = SharedModelModel(generatedId.toInt, usuario_id, archivo_id)
              Right(comparitdo)
            } else {
              Left("No se pudo agregar el compartido")
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


  def eliminarCompartido(id: Int): Future[Either[String, SharedModel]] = {
    Future {
      try {
        // Realizar la actualización para cambiar el campo "habilitado" a false
        val resultado = sql"DELETE from compartido WHERE id = $id".update()

        if (resultado > 0) {

          val resultadoFuture: Future[Either[String, FileModel]] = obtenerCompartidosPorUsuario(id)

          val resultado: Either[String, FileModel] = Await.result(resultadoFuture, 5.seconds)

          resultado match {
            case Right(fileModel) =>
              // Aquí puedes trabajar con el resultado Right (éxito)
              // Por ejemplo, imprimir el archivo
              println(s"Archivo encontrado: $fileModel")
              Right(fileModel)
            case Left(errorMessage) =>
              // Aquí puedes manejar el caso Left (error)
              // Por ejemplo, imprimir el mensaje de error
              println(s"Error: $errorMessage")
              Left(errorMessage)
          }
          

        } else {
          // La actualización no afectó ninguna fila, devolver un mensaje de error
          Left("No se pudo eliminar el archivo, ID no encontrado o ya está deshabilitado")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}")
          Left("Error interno del servidor")
      }
    }
  }

  def obtenerCompartidosPorUsuario(usuario_id: Int): Future[Either[String, List[SharedModel]]] = {
    Future {
      try {
        val archivos = sql"SELECT * FROM compartido WHERE usuario_id = $usuario_id"
          .map { rs =>
            SharedModel(
              rs.int("id"),
              rs.int("usuario_id"),
              rs.int("archivo_id")
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

}
