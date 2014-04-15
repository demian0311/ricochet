package actors

import akka.actor.Actor
import utils.{TimerEventRequest, TimerEventPost, TimerEvent}
import play.api.mvc.{Results, Result}
import scala.collection.mutable
import play.api.libs.json.{Writes, Json, JsValue}

class PersistorActor extends Actor with Results {
  val gauge = collection.mutable.Map[String, collection.mutable.MutableList[TimerEvent]]()

  implicit val TimerEventWrites = new Writes[TimerEvent] {
    def writes(timerEvent: TimerEvent) = Json.obj(
      "duration" -> timerEvent.duration,
      "dateTime" -> timerEvent.dateTime
    )
  }

  def receive = {
    case TimerEventPost(path, TimerEvent(duration, dateTime)) => {
      println("TimerEventPost: " + path)
      persist(TimerEventPost(path, TimerEvent(duration, dateTime)))
      showState()
    }
    case TimerEventRequest(path) => {
      println("TimerEventRequest: " + path)
      sender.forward(report(TimerEventRequest(path)))
    }
    case _ => {
      println("don't know how to handle")
    }
  }

  def persist(timerEventPost: TimerEventPost) {
    Thread.sleep(500)

    gauge.get(timerEventPost.path) match {
      case Some(gaugeList) => gaugeList += timerEventPost.timerEvent
      case None => gauge += (timerEventPost.path -> mutable.MutableList(timerEventPost.timerEvent))
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

  def showState(): Unit = {
    println("gauge: " + gauge)
  }
}
