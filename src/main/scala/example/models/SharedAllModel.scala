package models

case class SharedModel(
    id: Int,
    usuario_id: Int,
    archivo_id: Int
)

case class SharedCreateModel(
    usuario_id: Int,
    archivo_id: Int
)

case class SharedDeleteModel(
    id: Int
)

case class FileModelShared(
    id: Int,
    nombre: String,
    ruta: String,
    tamano: Double,
    usuario_id: Int,
    habilitado: Boolean,
    nodo_id: Int,
    directorio_id: Int,
    respaldo_id: Int,
    shared_id: Int
)
