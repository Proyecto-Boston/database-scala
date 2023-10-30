package controllers

import scalikejdbc._
import models.DirectoryModel
import models.Directorysearch
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
            DirectoryModel(
              rs.int("id"),
              rs.string("nombre"),
              rs.string("ruta"),
              rs.int("usuario_id"),
              rs.double("tamano"),
              rs.int("nodo_id"),
              rs.int("padre_id"),
              rs.boolean("habilitado"),
              rs.int("respaldo_id")
            )
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
  def buscarSubDirectorio(padre_id: Int): Future[Either[String, List[DirectoryModel]]] = {
    Future {
      try {
        val directorioOption =
          sql"SELECT * FROM directorios WHERE padre_id = $padre_id AND habilitado = 1"
            .map { rs =>
              DirectoryModel(
                rs.int("id"),
                rs.string("nombre"),
                rs.string("ruta"),
                rs.int("usuario_id"),
                rs.double("tamano"),
                rs.int("nodo_id"),
                rs.int("padre_id"),
                rs.boolean("habilitado"),
                rs.int("respaldo_id")
              )
            }
            .list()

        Right(directorioOption)

      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
          Left("Error interno del servidor")
      }
    }
  }

  def buscarDirectorioRoot(usuario_id: Int): Future[Either[String, List[DirectoryModel]]] = {
    Future {
      try {
        val directorioOption =
          sql"SELECT * FROM directorios WHERE padre_id = 0 AND habilitado = 1 AND usuario_id = $usuario_id"
            .map { rs =>
              DirectoryModel(
                rs.int("id"),
                rs.string("nombre"),
                rs.string("ruta"),
                rs.int("usuario_id"),
                rs.double("tamano"),
                rs.int("nodo_id"),
                rs.int("padre_id"),
                rs.boolean("habilitado"),
                rs.int("respaldo_id")
              )
            }
            .list()

        Right(directorioOption)

      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}") // Imprime detalles del error
          Left("Error interno del servidor")
      }
    }
  }

  def guardarDirectorios(
      directorios: List[(String, String, Int, Int, Int)]
  ): Future[List[Either[String, DirectoryModel]]] = {
    Future.sequence {
      directorios.map { case (nombre, ruta, usuario_id, nodo_id, respaldo_id) =>
        Future {
          try {
            val result =
              sql"INSERT INTO directorios (nombre, ruta, usuario_id, nodo_id, respaldo_id) VALUES ($nombre, $ruta, $usuario_id, $nodo_id, $respaldo_id)"
                .update()

            if (result > 0) {
              // Recupera el ID generado por la base de datos
              val generatedId: Long = sql"SELECT LAST_INSERT_ID()".map(rs => rs.long(1)).single().getOrElse(0L)

              // Crea una instancia de DirectoryModel con el ID real
              val directorio =
                DirectoryModel(generatedId.toInt, nombre, ruta, usuario_id, 0.0, nodo_id, 0, true, respaldo_id)
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
      subdirectorios: List[(String, String, Int, Int, Int, Int)]
  ): Future[List[Either[String, DirectoryModel]]] = {
    Future.sequence {
      subdirectorios.map { case (nombre, rutaPadre, usuario_id, nodo_id, padre_id, respaldo_id) =>
        Future {
          try {
            val nuevaRuta = s"$rutaPadre/$nombre"

            val result =
              sql"INSERT INTO directorios (nombre, ruta, usuario_id, nodo_id, padre_id, respaldo_id) VALUES ($nombre, $nuevaRuta, $usuario_id, $nodo_id, $padre_id, $respaldo_id)"
                .update()

            if (result > 0) {
              // Recupera el ID generado por la base de datos
              val generatedId: Long = sql"SELECT LAST_INSERT_ID()".map(rs => rs.long(1)).single().getOrElse(0L)

              // Crea una instancia de DirectoryModel con el ID real
              val directorio =
                DirectoryModel(
                  generatedId.toInt,
                  nombre,
                  nuevaRuta,
                  usuario_id,
                  0.0,
                  nodo_id,
                  padre_id,
                  true,
                  respaldo_id
                )
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
        val disableResult = sql"UPDATE directorios SET habilitado = false WHERE padre_id = $idDirectorio"
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

  def borrarDirectorio(id: Int): Future[Either[String, String]] = {
    Future {
      try {
        // Verifica si el directorio existe
        val dirExists = sql"SELECT count(*) FROM directorios WHERE id = $id".map(_.int(1)).single()

        if (dirExists.getOrElse(0) > 0) {
          // Deshabilita el directorio
          val disableSubD = sql"UPDATE directorios SET habilitado = false WHERE id = $id".update()

          if (disableSubD > 0) {
            // Verifica si hay archivos en el directorio
            val fileCount = sql"SELECT count(*) FROM archivos WHERE directorio_id = $id".map(_.int(1)).single()

            if (fileCount.getOrElse(0) > 0) {
              // Si hay archivos, intenta deshabilitarlos
              val disableResult = sql"UPDATE archivos SET habilitado = false WHERE directorio_id = $id".update()
              if (disableResult > 0) {
                // Directorio y su contenido deshabilitados correctamente
                Right("Directorio y su contenido deshabilitados correctamente")
              } else {
                // No se encontró el archivo o no se pudo deshabilitar
                Left("No se pudo deshabilitar el directorio y su contenido")
              }
            } else {
              // Si no hay archivos, considera la operación exitosa
              Right("Directorio y su contenido deshabilitados correctamente")
            }

            // Verifica si existen subdirectorios
            val subDirCount = sql"SELECT count(*) FROM directorios WHERE padre_id = $id".map(_.int(1)).single()

            if (subDirCount.getOrElse(0) > 0) {
              // Si existen subdirectorios, intenta deshabilitarlos
              val disableF = sql"UPDATE directorios SET habilitado = false WHERE padre_id = $id".update()

              if (disableF > 0) {
                // Verifica si hay archivos en los subdirectorios
                val subFileCount =
                  sql"SELECT count(*) FROM archivos WHERE directorio_id IN (SELECT id FROM directorios WHERE padre_id = $id)"
                    .map(_.int(1))
                    .single()

                if (subFileCount.getOrElse(0) > 0) {
                  // Si hay archivos, intenta deshabilitarlos
                  val disableSubFiles =
                    sql"UPDATE archivos SET habilitado = false WHERE directorio_id IN (SELECT id FROM directorios WHERE padre_id = $id)"
                      .update()
                  if (disableSubFiles > 0) {
                    Right("Subdirectorios y sus contenidos deshabilitados correctamente")
                  } else {
                    Left("No se pudieron deshabilitar los archivos de los subdirectorios")
                  }
                } else {
                  // Si no hay archivos, considera la operación exitosa
                  Right("Subdirectorios y sus contenidos deshabilitados correctamente")
                }
              } else {
                Left("No se pudieron deshabilitar los subdirectorios")
              }
            } else {
              // Si no existen subdirectorios, considera la operación exitosa
              Right("Directorio deshabilitado, No existen subdirectorios para deshabilitar")
            }
          } else {
            // No se encontraron directorio o no se pudieron deshabilitar
            Left("No se pudieron deshabilitar los subdirectorios")
          }
        } else {
          Left("El ID del directorio no existe")
        }

      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}")
          Left("Error interno del servidor")
      }
    }
  }
  def renombrarDirectorio(id: Int, nuevoNombre: String): Future[Either[String, String]] = {
    Future {
      try {
        // Realizar la actualización en la base de datos
        val resultado =
          sql"UPDATE directorios SET nombre = $nuevoNombre WHERE id = $id".update()

        if (resultado > 0) {
          Right("Renombre exitoso")

        } else {
          Left("No se pudo renombrar el directorio")
        }
      } catch {
        case e: Exception =>
          println(s"Error interno del servidor: ${e.getMessage}")
          Left("Error interno del servidor")
      }
    }
  }

}
