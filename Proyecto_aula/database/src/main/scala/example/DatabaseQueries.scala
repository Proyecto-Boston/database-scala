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

  case class Usuario(id: Int, nombre: String, apellido: String)

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

  /*def init() = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn =>
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

  // Métodos

  def registrarUsuario(id: Int, nombre: String, email: String, password: String)(implicit session: DBSession = AutoSession) = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn =>
      sql"INSERT INTO usuarios VALUES ($id, $nombre, $email, $password)"
        .update().apply()
    }
  }

  def buscarUsuario(id: Int) = { 
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection)(implicit session: DBSession = AutoSession) = { conn =>
      sql"SELECT * FROM usuarios WHERE id = $id"  
        .map( rs =>
          (rs.int("id"), rs.string("nombre"), rs.string("apellido"))
        ).single().apply()
    }
  }  

  def crearDirectorio(nombre:String, ruta:String, usuarioId: Int) = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn =>
      sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $ruta, $usuarioId)"
        .update().apply() 
    }
  }

  def crearSubDirectorio(nombre:String, rutaPadre:String, usuarioId: Int) = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn =>
      val nuevaRuta = s"$rutaPadre/$nombre"
      sql"INSERT INTO directorios (nombre, ruta, usuario_id) VALUES ($nombre, $nuevaRuta, $usuarioId)"
        .update().apply()
    }
  }

  def guardarArchivo(nombre:String, ruta:String, tamano:Long, checksum:String, usuarioId: Int) = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn =>
      sql"INSERT INTO archivos (nombre, ruta, tamano, checksum, usuario_id) VALUES ($nombre, $ruta, $tamano, $checksum, $usuarioId)"
        .update().apply()
    }
  }

  def descargarArchivo(id: Int) = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn =>
      sql"SELECT * FROM archivos WHERE id = $id"
        .map(rs => 
          (rs.int("id"), rs.string("nombre"), rs.string("ruta"), rs.long("tamano"), rs.string("checksum"), rs.int("usuario_id"))
        ).single().apply()  
    }
  }

  def moverArchivo(id: Int, nuevaRuta: String) = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn =>
      sql"UPDATE archivos SET ruta = $nuevaRuta WHERE id = $id"
        .update().apply()
    }
  }

  def eliminarArchivo(id: Int) = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn =>
      sql"DELETE FROM archivos WHERE id = $id"  
        .update().apply()
    }
  }

  def compartirArchivo(idArchivo: Int, idUsuario: Int) = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn =>
      sql"INSERT INTO compartidos (archivo_id, usuario_id) VALUES ($idArchivo, $idUsuario)"
        .update().apply()
    }
  }

  def reporteEspacio() = {
    ConnectionPool.borrowConnection(DatabaseConnectionManager.getConnection) { conn => 
      sql"SELECT usuario_id, SUM(tamano) AS espacio FROM archivos GROUP BY usuario_id"
        .map(rs =>  
          (rs.int("usuario_id"), rs.long("espacio")) 
        ).list().apply()
    }
  }
*/
}