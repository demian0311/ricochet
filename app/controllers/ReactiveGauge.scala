package controllers

import play.api.mvc._
import play.libs.Akka
import actors.PersistorActor
import akka.actor.Props
import akka.util.Timeout
import org.joda.time.DateTime
import akka.pattern.ask
import play.libs.Akka._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import utils.{TimerEventRequest, TimerEvent, TimerEventPost}

import scala.concurrent.ExecutionContext.Implicits.global

object ReactiveGauge extends Controller {

  val persistorActor = Akka.system.actorOf(Props[PersistorActor], name = "persistorActor")
  implicit val timeout = Timeout(5000)

  def get(path: String) = Action.async { request =>
    //val future: Future[Any] = persistorActor ? TimerEventRequest(path)
    val futureOfSimpleResult: Future[SimpleResult] = persistorActor.ask(TimerEventRequest(path)).mapTo[SimpleResult]
    //future.map( f => Ok("foo: " + f))

  /*
    val foo = for{
      theResponse <- future
    } yield theResponse
    */

    //future.map( f => f)
    //theResponse
    //future.map(f => Ok("f: " + f))

    futureOfSimpleResult
  }

  def post(path: String) = Action { implicit request =>
    val durationOpt = for{
      json <- request.body.asJson
      duration <- json.\("duration").asOpt[Int]
    } yield duration

    persistorActor ! TimerEventPost(path, TimerEvent(durationOpt.get, DateTime.now()))
    Ok("")
  }
}
