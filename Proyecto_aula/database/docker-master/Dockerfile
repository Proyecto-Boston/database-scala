# Usa la imagen base de MariaDB
FROM mariadb:latest

# Copia el script de inicialización al contenedor
COPY init.sql /docker-entrypoint-initdb.d/
