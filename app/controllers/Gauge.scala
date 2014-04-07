package controllers

import play.api._
import play.api.mvc._
import org.joda.time.DateTime
import play.api.libs.json._
import scala.collection.mutable

object Gauge extends Controller {

  val gauge = collection.mutable.Map[String, collection.mutable.MutableList[TimerEvent]]()

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
          "timerEvents" -> Json.toJson(gauge(path))
        )

        Ok(jsonOut)
      }
      case None => NotFound("couldn't find gauge for: " + path)
    }
  }

  def post(path: String) = Action { request =>
    val durationOpt = for{
      json <- request.body.asJson
      duration <- json.\("duration").asOpt[Int]
    } yield duration

    durationOpt match {
      case Some(duration) => {
        gauge.get(path) match {
          case Some(gaugeList) => {
            gaugeList += TimerEvent(duration)
            Ok("")
          }
          case None => {
            gauge += (path -> mutable.MutableList(TimerEvent(duration)))
            Created("path: " + path)
            }
          }
        }
      case None => BadRequest("couldn't parse duration from JSON")
    }
  }
}

case class TimerEvent(duration: Int, dateTime: DateTime = DateTime.now())
