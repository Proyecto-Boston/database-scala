package models

case class FileModel(
    id: Int,
    nombre: String,
    ruta: String,
    tamano: Double,
    usuario_id: Int,
    habilitado: Boolean,
    nodo_id: Int,
    directorio_id: Int
)

case class FileReportModel(usuario_id: Int, tamano: Double)

case class FileMoveModel(id: Int, nuevaRuta: String)

case class FileCreateModel(
    nombre: String,
    ruta: String,
    tamano: Double,
    usuario_id: Int,
    nodo_id: Int,
    directorio_id: Int
)
