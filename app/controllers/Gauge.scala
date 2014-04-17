package controllers

import play.api.mvc._
import play.api.libs.json._
import scala.collection.mutable
import utils.{TimerEventRequest, TimerEventPost, TimerEvent}

object Gauge extends Controller {
  val gauge = collection.mutable.Map[String, collection.mutable.MutableList[TimerEvent]]()

  implicit val TimerEventWrites = new Writes[TimerEvent] {
    def writes(timerEvent: TimerEvent) = Json.obj(
      "duration" -> timerEvent.duration,
      "dateTime" -> timerEvent.dateTime
    )
  }

  def get(path: String) = Action { request =>
    report(TimerEventRequest(path))
  }

  def post(path: String) = Action { request =>
    val durationOpt = for{
      json <- request.body.asJson
      duration <- (json \ "duration").asOpt[Int]
    } yield duration

    durationOpt match {
      case Some(duration) => persist(TimerEventPost(path, TimerEvent(duration)))
      case None => BadRequest("couldn't parse duration from JSON")
    }
  }

  def report(timerEventRequest: TimerEventRequest): Result = {
    Thread.sleep(500)

    gauge.get(timerEventRequest.path) match {
      case Some(listData) => {
        val jsonOut: JsValue = Json.obj(
          "path" -> timerEventRequest.path,
          "count" -> listData.size,
          "max" -> listData.map(_.duration).max,
          "min" -> listData.map(_.duration).min,
          "timerEvents" -> Json.toJson(listData)
        )

        Ok(jsonOut)
      }
      case None => NotFound("couldn't find gauge for: " + timerEventRequest.path)
    }
  }

  def persist(timerEventPost: TimerEventPost): Result = {
    Thread.sleep(500)

    this.synchronized {
      gauge.get(timerEventPost.path) match {
        case Some(gaugeList) => {
          gaugeList += timerEventPost.timerEvent
          Ok("path: " + timerEventPost.path)
        }
        case None => {
          gauge += (timerEventPost.path -> mutable.MutableList(timerEventPost.timerEvent))
          Created("path: " + timerEventPost.path)
        }
      }
    }
  }
}

