package controllers

import play.api.mvc._
import actors.PersistorActor
import akka.actor.{ActorSystem, ActorRef, Props}
import akka.util.Timeout
import akka.pattern.ask
import utils.{TimerEventRequest, TimerEvent, TimerEventPost}
import akka.routing.RoundRobinRouter

object ReactiveGauge extends Controller {

  implicit val timeout = Timeout(10000)

  // http://blog.evilmonkeylabs.com/2013/01/17/Distributing_Akka_Workloads_And_Shutting_Down_After/
  val system = ActorSystem("SimpleSystem")
  val persistorActor: ActorRef = system.actorOf(Props[PersistorActor].withRouter(
    RoundRobinRouter(nrOfInstances = 100)
  ), name = "simpleRoutedActor")

  def get(path: String) = Action.async { request =>
    // the implicit timeout is used
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
