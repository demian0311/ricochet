import actors.PersistorActor
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import org.joda.time.DateTime
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import akka.pattern.ask

import play.api.test._
import utils.{TimerEventRequest, TimerEvent, TimerEventPost}

@RunWith(classOf[JUnitRunner])
class PersistorActorSpec extends Specification {

  "PersistorActor" should {
    "receive a TimerEventPost" in new WithApplication {
      val system = ActorSystem("test")
      val persistorActor = system.actorOf(Props[PersistorActor], name = "persistorActor")
      val result = persistorActor ! TimerEventPost("foo/bar", TimerEvent(20, DateTime.now()))
    }


    "respond to query" in new WithApplication {
      //val inbox = inbox()
      val system = ActorSystem("test")
      val persistorActor = system.actorOf(Props[PersistorActor], name = "persistorActor")

      val timerEventRequest: TimerEventRequest = TimerEventRequest("foo/bar")


      implicit val timeout = Timeout(5000)
      val response = persistorActor ? timerEventRequest
      println("response: " + response)
      /*
      response.onSuccess{
        theResponse -> println("theResponse: " + theResponse)
      }*/
    }
  }
}
