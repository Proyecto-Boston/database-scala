/* package example

import example.{DatabaseConnectionManager => ConnectionManager}
import scalikejdbc._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import com.typesafe.config.ConfigFactory
import java.sql.Connection
import javax.sql.DataSource
import org.mariadb.jdbc.MariaDbPoolDataSource


object DatabaseQueries {
  Class.forName("org.mariadb.jdbc.Driver")
  val config = ConfigFactory.load()
  val url = ConnectionManager.dbUrl
  val user = ConnectionManager.dbUser
  val password = ConnectionManager.dbPassword

  ConnectionPool.singleton(url, user, password)


  // Conexión
  val urlMaestro = config.getString("db.urlMaestro")
  val urlEsclavo = config.getString("db.urlEsclavo")
  var urlActiva = urlMaestro
  case class Usuario(id: Int, nombre: String, apellido: String)
  case class Archivos(id: Int, nombre: String, ruta: String, tamaño: Double, usuario_id: Int)

  // Conexión maestro y esclavo

  /*
  def init() = {
      conmutarMaestroEsclavo()
    }
  }

  def conmutarMaestroEsclavo() = {
    if(!pingMaestro()) {
      urlActiva = urlEsclavo 
    }
  }

  def pingMaestro(): Boolean = {
    try {
      DriverManager.getConnection(urlMaestro, "user", "password")
      true
    } catch {
      case _: Exception => false
    }
  } 
  */

  // Métodos

  def buscarUsuario(id: Int)(implicit session: DBSession = AutoSession): Either[String, Json] = {
    try {
      val usuarioOption = sql"SELECT * FROM usuarios WHERE id = $id"
        .map { rs =>
          Usuario(rs.int("id"), rs.string("nombre"), rs.string("apellido"))
        }.single().map { usuario =>
          // Respuesta exitosa con estado 200 y JSON de usuario
          val jsonUsuario = Json.obj(
            "id" -> usuario.id.asJson,
            "nombre" -> usuario.nombre.asJson,
            "apellido" -> usuario.apellido.asJson
          )
          Right(jsonUsuario)
        }

      usuarioOption.getOrElse {
        // Usuario no encontrado con código 404
        Left("Usuario no encontrado")
      }
    } catch {
      case e: Exception =>
        // Error interno del servidor con código 500
        Left("Error interno del servidor")
    }
  }


  def registrarUsuario(nombre: String, apellido: String)(implicit session: DBSession = AutoSession): Either[String, Json] = {
    try {
      val result = sql"INSERT INTO usuarios (nombre, apellido) VALUES ($nombre, $apellido)"
        .update()

      if (result > 0) {
        // Respuesta exitosa con estado 200 y JSON de usuario
        val jsonUsuario = Json.obj(
          "nombre" -> nombre.asJson,
          "apellido" -> apellido.asJson
        )
        Right(jsonUsuario)
      } else {
        // No se pudo insertar el usuario
        Left("No se pudo insertar el usuario")
      }
    } catch {
      case e: Exception =>
        // Error interno del servidor con código 500
        Left("Error interno del servidor")
    }
  }

  

  def crearDirectorio(nombre: String, ruta: String, usuarioId: Int)(implicit session: DBSession = AutoSession): Either[String, Json] = {
    try {
      val result = sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $ruta, $usuarioId)"
        .update()

      if (result > 0) {
        // Respuesta exitosa con estado 200 y JSON del directorio
        val jsonDirectorio = Json.obj(
          "nombre" -> nombre.asJson,
          "ruta" -> ruta.asJson,
          "usuarioId" -> usuarioId.asJson
        )
        Right(jsonDirectorio)
      } else {
        // No se pudo insertar el directorio
        Left("No se pudo insertar el directorio")
      }
    } catch {
      case e: Exception =>
        // Error interno del servidor con código 500
        Left("Error interno del servidor")
    }
  }

  

  def crearSubDirectorio(nombre: String, rutaPadre: String, usuarioId: Int)(implicit session: DBSession = AutoSession): Either[String, Json] = {
    try {
      val nuevaRuta = s"$rutaPadre/$nombre"
      val result = sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $nuevaRuta, $usuarioId)"
        .update()

      if (result > 0) {
        // Respuesta exitosa con estado 200 y JSON del subdirectorio
        val jsonSubDirectorio = Json.obj(
          "nombre" -> nombre.asJson,
          "ruta" -> nuevaRuta.asJson,
          "usuarioId" -> usuarioId.asJson
        )
        Right(jsonSubDirectorio)
      } else {
        // No se pudo insertar el subdirectorio
        Left("No se pudo insertar el subdirectorio")
      }
    } catch {
      case e: Exception =>
        // Error interno del servidor con código 500
        Left("Error interno del servidor")
    }
  }

  

  def guardarArchivo(nombre: String, ruta: String, tamano: Double, usuarioId: Int)(implicit session: DBSession = AutoSession): Either[String, Json] = {
    try {
      val result = sql"INSERT INTO archivos (nombre, ruta, tamaño, usuario_id) VALUES ($nombre, $ruta, $tamano, $usuarioId)"
        .update()

      if (result > 0) {
        // Respuesta exitosa con estado 200 y JSON del archivo
        val jsonArchivo = Json.obj(
          "nombre" -> nombre.asJson,
          "ruta" -> ruta.asJson,
          "tamano" -> tamano.asJson,
          "usuarioId" -> usuarioId.asJson
        )
        Right(jsonArchivo)
      } else {
        // No se pudo guardar el archivo
        Left("No se pudo guardar el archivo")
      }
    } catch {
      case e: Exception =>
        // Error interno del servidor con código 500
        Left("Error interno del servidor")
    }
  }

  

  def descargarArchivo(id: Int)(implicit session: DBSession = AutoSession): Either[String, Json] = {
    try{
      val archivoOption = sql"SELECT * FROM archivos WHERE id = $id"
        .map{ rs =>
          Archivos(rs.int("id"), rs.string("nombre"), rs.string("ruta"), rs.double("tamaño"), rs.int("usuario_id"))
        }.single().map { archivo => 
        // Respuesta exitosa con estado 200 y JSON de usuario
        val jsonArchivo = Json.obj(
          "id" -> archivo.id.asJson,
          "nombre" -> archivo.nombre.asJson,
          "ruta" -> archivo.ruta.asJson,
          "tamaño" -> archivo.tamaño.asJson,
          "usuario_id" -> archivo.usuario_id.asJson
        )
        Right(jsonArchivo)
      }

      archivoOption.getOrElse {
        // Archivo no encontrado con código 404
        Left("Archivo no encontrado")
      }
    } catch {
      case e: Exception =>
        // Error interno del servidor con código 500
        Left("Error interno del servidor")
    }
  }
    

  def moverArchivo(id: Int, nuevaRuta: String)(implicit session: DBSession = AutoSession): Either[String, Json] = {
  try {
    val resultado = sql"UPDATE archivos SET ruta = $nuevaRuta WHERE id = $id".update()

    if (resultado > 0) {
      Right(Json.obj("filas_afectadas" -> resultado.asJson))
    } else {
      Left("Archivo no encontrado")
    }
  } catch {
    case e: Exception =>
      Left("Error interno del servidor")
  }
}


  

  def eliminarArchivo(id: Int)(implicit session: DBSession = AutoSession): Either[String, Json] = {
    try {
      val resultado = sql"DELETE FROM archivos WHERE id = $id"  
        .update()
      if(resultado > 0) {
        Right(Json.obj("filas_afectadas" -> resultado.asJson))
      } else {
        Left("Archivo no encontrado")
      }
    } catch {
      case e: Exception =>
        Left("Error interno del servidor")
    }
  }
  

  def compartirArchivo(idArchivo: Int, idUsuario: Int)(implicit session: DBSession = AutoSession): Either[String, Json] = {
    try{
      val resultado = sql"INSERT INTO compartidos (archivo_id, usuario_id) VALUES ($idArchivo, $idUsuario)"
        .update()
        if (resultado > 0) {
      Right(Json.obj("filas_afectadas" -> resultado.asJson))
    } else {
      Left("Archivo no encontrado")
    }
  } catch {
    case e: Exception =>
      Left("Error interno del servidor")
  }
}


  def reporteEspacio(usuario_id: Int)(implicit session: DBSession = AutoSession): Either[String, Json] = {
    try {
      val resultado = sql"SELECT usuario_id, SUM(tamaño) AS espacio FROM archivos WHERE usuario_id = $usuario_id"
        .map(rs =>  
          (rs.int("usuario_id"), rs.double("espacio")) 
        ).single()

      resultado match {
        case Some((usuario_id, espacio)) =>
          // Crear un objeto JSON a partir de los resultados
          val jsonResultado = Json.obj(
            "usuario_id" -> usuario_id.asJson,
            "espacio" -> espacio.asJson
          )

          Right(jsonResultado)
        case None =>
          // No se encontraron registros para el usuario con el id dado
          Left("No se encontraron registros para el usuario con el id dado")
      }
    } catch {
      case e: Exception =>
        // Error interno del servidor
        Left("Error interno del servidor")
    }
  }

}
 */