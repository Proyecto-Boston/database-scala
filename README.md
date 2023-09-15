# Base de datos (Scala)

## Conexion a la base de datos

1. crear una carpeta dentro de main con un archivo .conf

```bash
src/main/resources/application.conf
```

2. El contenido del archivo debe ser las credenciales

```bash
db {
  user = "root"
  password = "pontificie"
  urlMaestro = "jdbc:mariadb://localhost:3307/proyecto_aula"
  urlEsclavo = "jdbc:mariadb://localhost:3307/proyecto_aula"
}
```

## Ejecutar programa

```bash
sbt run
```

## Docker compose
[Docker compose](https://github.com/Proyecto-Boston/database-scala/blob/main/docker-compose.yml)

## Docker maestro
[Maestro](https://github.com/Proyecto-Boston/database-scala/tree/main/docker-master)

## Docker esclavo 
[Esclavo](https://github.com/Proyecto-Boston/database-scala/tree/main/docker-slave)

## Conexión a la base de datos
[Conexión](https://github.com/Proyecto-Boston/database-scala/blob/main/src/main/scala/example/DatabaseConfig.scala)

## Dependencias del proyecto
[Dependencias](https://github.com/Proyecto-Boston/database-scala/blob/main/build.sbt)