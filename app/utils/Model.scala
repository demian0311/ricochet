package utils

import org.joda.time.DateTime
import scala.collection.mutable
import play.api.mvc.{Results, Result}
import play.api.libs.json.{Writes, Json, JsValue}

case class TimerEventPost(path: String, timerEvent: TimerEvent)
case class TimerEvent(duration: Int, dateTime: DateTime = DateTime.now())
case class TimerEventRequest(path: String)

object GaugePersistence extends Results {

  implicit val TimerEventWrites = new Writes[TimerEvent] {
    def writes(timerEvent: TimerEvent) = Json.obj(
      "duration" -> timerEvent.duration,
      "dateTime" -> timerEvent.dateTime
    )
  }

  val mapOfTimerEvents = collection.mutable.Map[ String,  collection.mutable.MutableList[TimerEvent]]()
  //val mapOfTimerEvents = Map[ String,  collection.mutable.MutableList[TimerEvent]]()

  def persist(timerEventPost: TimerEventPost) {
    Thread.sleep(500)

    this.synchronized {
      mapOfTimerEvents.get(timerEventPost.path) match {
        case Some(gaugeList) => gaugeList += timerEventPost.timerEvent
        case None => mapOfTimerEvents += (timerEventPost.path -> mutable.MutableList(timerEventPost.timerEvent))
      }
    }
  }

  def report(timerEventRequest: TimerEventRequest): Result = {
    Thread.sleep(500)

    mapOfTimerEvents.get(timerEventRequest.path) match {
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



}
