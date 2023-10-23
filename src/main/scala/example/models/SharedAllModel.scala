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
