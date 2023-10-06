# Usar una imagen base oficial de Scala
FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1

# Definir el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar los archivos del proyecto al contenedor
COPY . /app

# Compilar la aplicación
RUN sbt update
RUN sbt compile

# Exponer el puerto por el que se comunicará la aplicación
EXPOSE 80

# Definir el comando para ejecutar la aplicación
CMD ["sbt", "run"]
