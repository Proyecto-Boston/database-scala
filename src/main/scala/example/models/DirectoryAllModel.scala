package models

case class DirectoryModel(
    id: Int,
    nombre: String,
    ruta: String,
    usuarioId: Int,
    tamano: Double,
    nodoId: Int
)
case class DirectoryCreateModel(nombre: String, ruta: String, usuario_id: Int, tamano: Double, nodo_id: Int)
