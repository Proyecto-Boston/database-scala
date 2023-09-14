package controllers

import scalikejdbc._
import models.UserModel
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global  // Agrega esta línea


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
          val usuario = UserModel(0, nombre, apellido) // Suponiendo que el ID es autoincremental
          Right(usuario)
        } else {
          Left("No se pudo insertar el usuario")
        }
      } catch {
  case e: Exception =>
    println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
    Left("Error interno del servidor")
}

    }
  }

  // Otros métodos del controlador
}
