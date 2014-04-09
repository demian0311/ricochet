package actors

import akka.actor.Actor
import utils.{TimerEventPost, TimerEvent}
import play.api.mvc.{Results, Result}
import scala.collection.mutable

class PersistorActor extends Actor with Results {
  // Results gets us the Ok & Created, probably a bad
  // practice to couple Actors with knowledge of web-tier

  // the data store is in the Actor, that's probably
  // not a good thing
  val gauge = collection.mutable.Map[String, collection.mutable.MutableList[TimerEvent]]()

  def receive = {
    case TimerEventPost(path, TimerEvent(duration, dateTime)) => {
      println("reactive: " + path)
      persist(TimerEventPost(path, TimerEvent(duration, dateTime)))
      showState()
    }
    //case TimerEventRequest(path)
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

  def showState(): Unit = {
    println("gauge: " + gauge)
  }
}
