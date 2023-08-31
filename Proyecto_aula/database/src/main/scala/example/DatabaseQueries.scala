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

  def buscarUsuario(id: Int)(implicit session: DBSession = AutoSession) = {

    sql"SELECT * FROM usuarios WHERE id = $id"
    .map{ rs =>
      Map(
        "id" -> rs.int("id"),
        "nombre" -> rs.string("nombre"), 
        "apellido" -> rs.string("apellido")
      )
    }.single().map(x => x)

  }

  def registrarUsuario(nombre: String, apellido: String)(implicit session: DBSession = AutoSession) = {
      sql"INSERT INTO usuarios (nombre, apellido) VALUES ($nombre, $apellido)"
        .update()
  }
  

  def crearDirectorio(nombre: String, ruta: String, usuarioId: Int)(implicit session: DBSession = AutoSession) = {
      sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $ruta, $usuarioId)"
        .update()
    }
  

  def crearSubDirectorio(nombre: String, rutaPadre: String, usuarioId: Int)(implicit session: DBSession = AutoSession) = {
      val nuevaRuta = s"$rutaPadre/$nombre"
      sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $nuevaRuta, $usuarioId)"
        .update()
    }
  

  def guardarArchivo(nombre: String, ruta: String, tamano: Double, usuarioId: Int)(implicit session: DBSession = AutoSession) = {
      sql"INSERT INTO archivos (nombre, ruta, tamaño, usuario_id) VALUES ($nombre, $ruta, $tamano, $usuarioId)"
        .update()
    }
  

  def descargarArchivo(id: Int)(implicit session: DBSession = AutoSession) = {
      sql"SELECT * FROM archivos WHERE id = $id"
      .map{ rs =>
      Map(
        "id" -> rs.int("id"),
        "nombre" -> rs.string("nombre"), 
        "ruta" -> rs.string("ruta"),
        "tamaño" -> rs.double("tamaño"),
        "usuario_id" -> rs.int("usuario_id")
      )
    }.single().map(x => x)
  }
    
  

  def moverArchivo(id: Int, nuevaRuta: String)(implicit session: DBSession = AutoSession) = {
      sql"UPDATE archivos SET ruta = $nuevaRuta WHERE id = $id"
        .update()
    }
  

  def eliminarArchivo(id: Int)(implicit session: DBSession = AutoSession) = {
      sql"DELETE FROM archivos WHERE id = $id"  
        .update()
    }
  

  def compartirArchivo(idArchivo: Int, idUsuario: Int)(implicit session: DBSession = AutoSession) = {
      sql"INSERT INTO compartidos (archivo_id, usuario_id) VALUES ($idArchivo, $idUsuario)"
        .update()
    }
  

  def reporteEspacio()(implicit session: DBSession = AutoSession) = {
      sql"SELECT usuario_id, SUM(tamaño) AS espacio FROM archivos GROUP BY usuario_id"
        .map(rs =>  
          (rs.int("usuario_id"), rs.double("espacio")) 
        ).list()
    }
  
}