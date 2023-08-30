package example

import example.DatabaseQueries  


object Main extends App {

  val idUsuario = 1
  val usuario = DatabaseQueries.buscarUsuario(idUsuario).get
  println(usuario)
  val registro = DatabaseQueries.registrarUsuario("nombre","apellido")
  println(registro)
  val busqueda = DatabaseQueries.
  
} 