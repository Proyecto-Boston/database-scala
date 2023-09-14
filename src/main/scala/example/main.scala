import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.UserModel
import routes.UserRoute
import controllers.UserController



object Main extends App {
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val userController = new UserController
  val userRoute = new UserRoute(userController)


  val bindingFuture = Http().bindAndHandle(userRoute.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  // Mantén el programa en ejecución
  scala.io.StdIn.readLine()
  bindingFuture.flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
