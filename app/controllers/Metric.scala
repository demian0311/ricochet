package controllers

import play.api._
import play.api.mvc._
import org.joda.time.DateTime
import play.api.libs.json._
import scala.collection.mutable

object Metric extends Controller {

  implicit val TimerEventWrites = new Writes[TimerEvent] {
    def writes(timerEvent: TimerEvent) = Json.obj(
      "duration" -> timerEvent.duration,
      "dateTime" -> timerEvent.dateTime
    )
  }

  val gauge = collection.mutable.Map[String, collection.mutable.MutableList[TimerEvent]]()

  def index(path: String) = Action { request =>
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
    request.body.asJson match {
      case Some(json) => {
        println("json: " + json)
        val maybeDuration = json.\("duration").asOpt[Int]

        maybeDuration match {
          case Some(duration) => {
            gauge.get(path) match {
              case Some(gaugeList) => {
                gaugeList += TimerEvent(duration, DateTime.now())

                Ok("got JSON: " + json)
              }
              case None => {
                gauge += (path -> mutable.MutableList(TimerEvent(duration, DateTime.now())))
                Created("path: " + path)
              }
            }
          }
          case None => BadRequest("couldn't parse duration from JSON")
        }
      }
      case None => BadRequest("POST should include JSON")
    }
  }
}

case class TimerEvent(duration: Int, dateTime: DateTime)


