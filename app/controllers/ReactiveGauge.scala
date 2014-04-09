package controllers

import play.api.mvc._
import org.joda.time.DateTime
import play.api.libs.json._
import akka.actor.Props
import play.libs.Akka
import actors.PersistorActor
import utils.{TimerEventPost, TimerEvent}

object ReactiveGauge extends Controller {

  val gauge = collection.mutable.Map[String, collection.mutable.MutableList[TimerEvent]]()
  val persistorActor = Akka.system.actorOf(Props[PersistorActor], name = "persistorActor")

  implicit val TimerEventWrites = new Writes[TimerEvent] {
    def writes(timerEvent: TimerEvent) = Json.obj(
      "duration" -> timerEvent.duration,
      "dateTime" -> timerEvent.dateTime
    )
  }

  def get(path: String) = Action { request =>
    gauge.get(path) match {
      case Some(listData) => {
        val jsonOut: JsValue = Json.obj(
          "path" -> path,
          "count" -> gauge(path).size,
          "max" -> gauge(path).map(_.duration).max,
          "min" -> gauge(path).map(_.duration).min,
          "timerEvents" -> Json.toJson(gauge(path))
        )

        Ok(jsonOut)
      }
      case None => NotFound("couldn't find gauge for: " + path)
    }
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
