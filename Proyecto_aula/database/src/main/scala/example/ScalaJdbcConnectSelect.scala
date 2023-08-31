package example

import example.DatabaseQueries  


object Main extends App {

  val usuario = DatabaseQueries.buscarUsuario(1).get
  println(usuario)
  val registro = DatabaseQueries.registrarUsuario("nombre","apellido")
  println(registro)
  val crearDirectorio = DatabaseQueries.crearDirectorio("nombre","ruta",1)
  println(crearDirectorio)
  val crearSubDirectorio = DatabaseQueries.crearSubDirectorio("nombre","ruta",1)
  println(crearSubDirectorio)
  val guardarArchivo = DatabaseQueries.guardarArchivo("nombre","ruta",34.563,1)
  println(crearSubDirectorio)
  val descargarArchivo = DatabaseQueries.descargarArchivo(1)
  println(descargarArchivo)
  val moverArchivo = DatabaseQueries.moverArchivo(1, "nuevaRuta")
  println(moverArchivo)
  val eliminarArchivo = DatabaseQueries.eliminarArchivo(6)
  println(eliminarArchivo)
  val compartirArchivo = DatabaseQueries.compartirArchivo(1, 1)
  println(compartirArchivo)
  val reporteEspacio = DatabaseQueries.reporteEspacio()
  println(reporteEspacio)

  
}  