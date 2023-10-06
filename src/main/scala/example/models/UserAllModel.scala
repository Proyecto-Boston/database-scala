package models

case class UserModel(auth_id: Int, nombre: String, apellido: String, habilitado: Boolean)

case class UserCreateModel(auth_id: Int, nombre: String, apellido: String)
