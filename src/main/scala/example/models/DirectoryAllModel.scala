package models

case class DirectoryModel(
    id: Int,
    nombre: String,
    ruta: String,
    usuarioId: Int,
    tamano: Double,
    nodoId: Int,
    padreId: Int,
    habilitado: Boolean,
    respado_id: Int
)
case class DirectoryCreateModel(nombre: String, ruta: String, usuario_id: Int, nodo_id: Int, respaldo_id: Int)

case class SubDirectoryCreateModel(
    nombre: String,
    ruta: String,
    usuario_id: Int,
    nodo_id: Int,
    padre_id: Int,
    respaldo_id: Int
)

case class Directorysearch(usuario_id: Int, padre_id: Int)
