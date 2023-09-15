package controllers

import scalikejdbc._
import models.UserModel
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class UserController {
  implicit val session: DBSession = AutoSession

  def buscarUsuario(id: Int): Future[Either[String, UserModel]] = {
    Future {
      try {
        val usuarioOption = sql"SELECT * FROM usuarios WHERE id = $id"
          .map { rs =>
            UserModel(rs.int("id"), rs.string("nombre"), rs.string("apellido"))
          }.single().map { usuario =>
            // Respuesta exitosa con estado 200 y JSON de usuario
            Right(usuario)
          }

        usuarioOption.getOrElse {
          // Usuario no encontrado con código 404
          Left("Usuario no encontrado")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
          Left("Error interno del servidor")
      }
    }
  }

  def registrarUsuario(nombre: String, apellido: String): Future[Either[String, UserModel]] = {
    Future {
      try {
        val result: Int = sql"INSERT INTO usuarios (nombre, apellido) VALUES ($nombre, $apellido)"
          .update()

        if (result > 0) {
          // Recupera el ID generado por la base de datos
          val generatedId: Long = sql"SELECT LAST_INSERT_ID()".map(rs => rs.long(1)).single().getOrElse(0L)

          // Crea una instancia de UserModel con el ID real
          val usuario = UserModel(generatedId.toInt, nombre, apellido)
          Right(usuario)
        } else {
          Left("No se pudo insertar el usuario")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}")
          Left("Error interno del servidor")
      }
    }
  }

  def EliminarUsuario(id: Int): Future[Either[String, UserModel]] = {
    Future {
      try {
        val usuarioOption = sql"SELECT * FROM usuarios WHERE id = $id"
          .map { rs =>
            UserModel(rs.int("id"), rs.string("nombre"), rs.string("apellido"))
          }.single().map { usuario =>
            // Respuesta exitosa con estado 200 y JSON de usuario
            Right(usuario)
          }

        usuarioOption.getOrElse {
          // Usuario no encontrado con código 404
          Left("Usuario no encontrado")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
          Left("Error interno del servidor")
      }
    }
  }
}
