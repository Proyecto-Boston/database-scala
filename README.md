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

## Instalación de Docker, MariaDB y Docker Compose en Ubuntu

**Paso 1: Instalar Docker**
1. Actualiza tu lista de paquetes existente:
```bash
sudo apt-get update
```
2. Instala los paquetes necesarios que permiten a apt usar paquetes a través de HTTPS:
```bash
sudo apt-get install apt-transport-https ca-certificates curl software-properties-common
```
3. Agrega la clave GPG para el repositorio oficial de Docker a tu sistema:
```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```
4. Agrega el repositorio de Docker a las fuentes de APT:
```bash
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
```
5. Actualiza el paquete de la base de datos con los paquetes Docker del repositorio recién agregado:
```bash
sudo apt-get update
```
6. Asegúrate de que vas a instalar desde el repositorio de Docker en lugar del repositorio predeterminado de Ubuntu:
```bash
apt-cache policy docker-ce
```
7. Finalmente, instala Docker:
```bash
sudo apt-get install -y docker-ce
```
8. Docker ahora debería estar instalado, el demonio iniciado y el proceso habilitado para iniciar en el arranque. Verifica que esté funcionando:
```bash
sudo systemctl status docker
```

**Paso 2: Instalar MariaDB**
1. Actualiza tu lista de paquetes existente:
```bash
sudo apt-get update
```
2. Luego instala MariaDB con el siguiente comando:
```bash
sudo apt-get install mariadb-server
```
3. Asegúrate de que MariaDB esté funcionando con el comando systemctl start:
```bash
sudo systemctl start mariadb.service
```

**Paso 3: Instalar Docker Compose**
1. Descarga la versión más reciente estable de Docker Compose ejecutando este comando:
```bash
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```
2. A continuación, aplica permisos ejecutables al binario:
```bash
sudo chmod +x /usr/local/bin/docker-compose
```

## Configuración de Replicación en MariaDB

1. Configuración del Maestro:

```bash
# Editar el archivo de configuración de MariaDB
sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf

# Establecer el identificador único del servidor (server_id)
server_id = 1
```

2. Configuración del Esclavo:

```bash
# Editar el archivo de configuración de MariaDB
sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf

# Establecer el identificador único del servidor (server_id)
server_id = 2
```

3. Creación de un Usuario de Replicación:

```bash
# Acceder a MariaDB como usuario root
mysql -u root -p

# Crear un usuario de replicación y asignarle los privilegios necesarios
CREATE USER 'replication_user'@'%' IDENTIFIED BY 'password';
GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'%';
```

4. Obtención de la Información del Maestro:

```bash
# Acceder a MariaDB como usuario root
mysql -u root -p

# Obtener la posición binaria actual y la identificación del maestro
SHOW MASTER STATUS;
```

5. Configuración del Esclavo con la Información del Maestro:

```bash
# Acceder a MariaDB como usuario root en el esclavo
mysql -u root -p

# Configurar el esclavo usando la información del maestro (posición binaria y identificación)
CHANGE MASTER TO MASTER_HOST='master_ip', MASTER_USER='replication_user', MASTER_PASSWORD='password', MASTER_LOG_FILE='binlog_file', MASTER_LOG_POS=binlog_position;
```

6. Iniciar la Replicación en el Esclavo:

```bash
# Acceder a MariaDB como usuario root en el esclavo
mysql -u root -p

# Iniciar la replicación en el esclavo
START SLAVE;
```

## Ejecución del Proyecto Scala en Docker

1. Construir la Imagen Docker:

```bash
# Construir la imagen Docker
docker build -t myproject .
```

2. Ejecutar el Contenedor Docker:

```bash
# Ejecutar el contenedor Docker
docker run -d -p 8080:8080 myproject
```

## Docker compose
[Docker-compose](https://github.com/Proyecto-Boston/database-scala/blob/main/docker-compose.yml)

## Docker maestro
[Dockerfile](https://github.com/Proyecto-Boston/database-scala/blob/main/Dockerfile)

## Conexión a la base de datos
[Conexión](https://github.com/Proyecto-Boston/database-scala/blob/main/src/main/scala/example/DatabaseConfig.scala)

## Dependencias del proyecto
[Dependencias](https://github.com/Proyecto-Boston/database-scala/blob/main/build.sbt)