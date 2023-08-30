package example

import example.DatabaseConnectionManager

import scalikejdbc._

object DatabaseQueries {
  Class.forName("org.mariadb.jdbc.Driver")
  
  val url = DatabaseConnectionManager.dbUrl
  val user = DatabaseConnectionManager.dbUser 
  val password = DatabaseConnectionManager.dbPassword

  ConnectionPool.singleton(url, user, password)  


  // Conexión 
  val urlMaestro = "jdbc:mariadb://url-maestro"
  val urlEsclavo = "jdbc:mariadb://url-esclavo" 
  var urlActiva = urlMaestro

  case class ID(id: Int)
  case class Usuario(id: Int, nombre: String, apellido: String)
  case class Directorios(nombre: String, ruta: String, usuarioId: Int)
  case class Archivo(nombre: String, ruta: String, tamano: Long, checksum: String, usuarioId: Int)
  case class Compartir(idArchivo: Int, idUsuario: Int)
  case class Mover(id: Int, nuevaRuta: String)

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

  def buscarUsuario(ID)(implicit session: DBSession = AutoSession) = {

    sql"SELECT * FROM usuarios WHERE id = $id"
    .map{ rs =>
      Map(
        "id" -> rs.int("id"),
        "nombre" -> rs.string("nombre"), 
        "apellido" -> rs.string("apellido")
      )
    }.single().map(x => x)

  }

  def registrarUsuario(Usuario)(implicit session: DBSession = AutoSession) = {
      sql"INSERT INTO usuarios (nombre, apellido) VALUES ($nombre, $apeliido)"
        .update()
  }
  

  def crearDirectorio(Directorios)(implicit session: DBSession = AutoSession) = {
      sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $ruta, $usuarioId)"
        .update()
    }
  

  def crearSubDirectorio(Directorios)(implicit session: DBSession = AutoSession) = {
      val nuevaRuta = s"$rutaPadre/$nombre"
      sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $nuevaRuta, $usuarioId)"
        .update()
    }
  

  def guardarArchivo(Archivo)(implicit session: DBSession = AutoSession) = {
      sql"INSERT INTO archivos (nombre, ruta, tamano, checksum, usuario_id) VALUES ($nombre, $ruta, $tamano, $checksum, $usuarioId)"
        .update()
    }
  

  def descargarArchivo(ID)(implicit session: DBSession = AutoSession) = {
      sql"SELECT * FROM archivos WHERE id = $id"
        .map(rs => 
          (rs.int("id"), rs.string("nombre"), rs.string("ruta"), rs.long("tamano"), rs.string("checksum"), rs.int("usuario_id"))
        ).single()
    }
  

  def moverArchivo(Mover)(implicit session: DBSession = AutoSession) = {
      sql"UPDATE archivos SET ruta = $nuevaRuta WHERE id = $id"
        .update()
    }
  

  def eliminarArchivo(ID)(implicit session: DBSession = AutoSession) = {
      sql"DELETE FROM archivos WHERE id = $id"  
        .update()
    }
  

  def compartirArchivo(Compartir)(implicit session: DBSession = AutoSession) = {
      sql"INSERT INTO compartidos (archivo_id, usuario_id) VALUES ($idArchivo, $idUsuario)"
        .update()
    }
  

  def reporteEspacio()(implicit session: DBSession = AutoSession) = {
      sql"SELECT usuario_id, SUM(tamano) AS espacio FROM archivos GROUP BY usuario_id"
        .map(rs =>  
          (rs.int("usuario_id"), rs.long("espacio")) 
        ).list()
    }
  
}