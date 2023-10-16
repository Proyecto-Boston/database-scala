package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory
import scalikejdbc.ConnectionPool
import controllers.{UserController, FileController, DirectoryController, SharedController}
import routes.{UserRoute, FileRoute, DirectoryRoute, SharedRoute}
import models.{UserModel, FileModel, DirectoryModel, SharedModel}
import example.DatabaseConfig

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // Inicializa el pool de conexiones llamando a initializeDatabase()
  DatabaseConfig.pingMaestro()

  val userController = new UserController
  val userRoute = new UserRoute(userController)

  val fileController = new FileController
  val fileRoute = new FileRoute(fileController)

  val directoryController = new DirectoryController
  val directoryRoute = new DirectoryRoute(directoryController)

  val sharedController = new SharedController
  val sharedRoute = new SharedRoute(sharedController)

  val routes: Route = userRoute.route ~ fileRoute.route ~ directoryRoute.route ~ sharedRoute.route

  val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", 80)
  println(s"Server online at http://localhost:80/")

  while (true) {
    Thread.sleep(10000)
  }
}
