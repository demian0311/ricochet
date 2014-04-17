package actors

import akka.actor.Actor
import utils._
import play.api.mvc.Results

class PersistorActor extends Actor with Results {

  def receive = {
    case TimerEventPost(path, TimerEvent(duration, dateTime)) => {
      GaugePersistence.persist(TimerEventPost(path, TimerEvent(duration, dateTime)))
    }
    case TimerEventRequest(path) => {
      sender.forward(GaugePersistence.report(TimerEventRequest(path)))
    }
  }
}
