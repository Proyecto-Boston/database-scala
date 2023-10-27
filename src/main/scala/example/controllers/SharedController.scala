//val resultado = sql"INSERT INTO compartidos (archivo_id, usuario_id) VALUES ($idArchivo, $idUsuario)"
package controllers

import scalikejdbc._
import models.SharedModel
import models.FileModelShared

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

class SharedController {
  implicit val session: DBSession = AutoSession

  def guardarCompartido(usuario_id: Int, archivo_id: Int): Future[Either[String, SharedModel]] = {

    Future {
      try {
        val result =
          sql"INSERT INTO compartidos (usuario_id, archivo_id) VALUES ($usuario_id, $archivo_id)"
            .update()

        if (result > 0) {
          // Recupera el ID generado por la base de datos
          val generatedId: Long = sql"SELECT LAST_INSERT_ID()".map(rs => rs.long(1)).single().getOrElse(0L)

          // Crea una instancia de DirectoryModel con el ID real
          val comparitdo = SharedModel(generatedId.toInt, usuario_id, archivo_id)
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

  def eliminarCompartido(id: Int): Future[Either[String, String]] = {
    Future {
      try {
        val resultado = sql"DELETE from compartidos WHERE id = $id".update()

        if (resultado > 0) {

          Right("Se eliminó el archivo correctamente")

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

  def obtenerCompartidosPorUsuario(usuario_id: Int): Future[Either[String, List[FileModelShared]]] = {
    Future {
      try {
        val archivosCompartidos = sql"SELECT * FROM compartidos WHERE usuario_id = $usuario_id"
          .map { rs =>
            val id = rs.int("id")
            val archivoId = rs.int("archivo_id")
            // Obtén los detalles del archivo basándote en el archivo_id
            sql"SELECT * FROM archivos WHERE id = $archivoId"
              .map(rs =>
                FileModelShared(
                  rs.int("id"),
                  rs.string("nombre"),
                  rs.string("ruta"),
                  rs.double("tamano"),
                  rs.int("usuario_id"),
                  rs.boolean("habilitado"),
                  rs.int("nodo_id"),
                  rs.int("directorio_id"),
                  rs.int("respaldo_id"),
                  id
                )
              )
              .single()
          }
          .list()

        // Filtra los None y obtén solo los Some[FileModel]
        val archivos = archivosCompartidos.flatten

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
