package models

case class UserModel(id: Int, nombre: String, apellido: String, habilitado: Boolean)

case class UserCreateModel(nombre: String, apellido: String)


