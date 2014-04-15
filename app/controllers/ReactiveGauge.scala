package controllers

import play.api.mvc._
import play.libs.Akka
import actors.PersistorActor
import akka.actor.Props
import akka.util.Timeout
import akka.pattern.ask
import utils.{TimerEventRequest, TimerEvent, TimerEventPost}

object ReactiveGauge extends Controller {

  val persistorActor = Akka.system.actorOf(Props[PersistorActor], name = "persistorActor")
  implicit val timeout = Timeout(5000)

  def get(path: String) = Action.async { request =>
    persistorActor.ask(TimerEventRequest(path)).mapTo[SimpleResult]
  }

  def post(path: String) = Action { implicit request =>
    val durationOpt = for{
      json <- request.body.asJson
      duration <- json.\("duration").asOpt[Int]
    } yield duration

    persistorActor ! TimerEventPost(path, TimerEvent(durationOpt.get))
    Ok("")
  }
}
