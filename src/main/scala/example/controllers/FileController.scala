package controllers

import scalikejdbc._
import models.FileModel
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class FileController {
  implicit val session: DBSession = AutoSession

  def buscarArchivo(id: Int): Future[Either[String, FileModel]] = {
    Future {
      try {
        val archivoOption = sql"SELECT * FROM archivos WHERE id = $id"
          .map { rs =>

            FileModel(rs.int("id"), rs.string("nombre"), rs.string("ruta"), rs.double("tamano"), rs.int("usuario_id"))
          }.single().map { file =>
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

  def guardarArchivo(nombre: String, ruta: String, tamano: Double, usuario_id: Int): Future[Either[String, FileModel]] = {
  Future {
    try {
      val result = sql"INSERT INTO archivos (nombre, ruta, usuario_id) VALUES ($nombre, $ruta, $tamano, $usuario_id)"
        .update()

      if (result > 0) {
        // Recupera el ID generado por la base de datos
        val generatedId: Long = sql"SELECT LAST_INSERT_ID()".map(rs => rs.long(1)).single().getOrElse(0L)

        // Crea una instancia de DirectoryModel con el ID real
        val archivo = FileModel(generatedId.toInt, nombre, ruta, tamano, usuario_id)
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
