import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import controllers.{UserController, FileController, DirectoryController}
import routes.{UserRoute, FileRoute, DirectoryRoute}
import models.{UserModel, FileModel, DirectoryModel}




object Main extends App {
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val userController = new UserController
  val userRoute = new UserRoute(userController)
  
  val fileController = new FileController
  val fileRoute = new FileRoute(fileController)

  val directoryController = new DirectoryController
  val directoryRoute = new DirectoryRoute(directoryController)

  val routes: Route = userRoute.route ~ fileRoute.route ~ directoryRoute.route
  
  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")

  // Mantén el programa en ejecución
  scala.io.StdIn.readLine()
  bindingFuture.flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
