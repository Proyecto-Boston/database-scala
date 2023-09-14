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
          }.single().map { directorio =>
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

  def guardarDirectorio(nombre: String, ruta: String, usuario_id: Int): Future[Either[String, DirectoryModel]] = {
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
