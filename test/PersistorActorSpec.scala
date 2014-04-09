import actors.PersistorActor
import akka.actor.{ActorSystem, Props}
import org.joda.time.DateTime
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import utils.{TimerEvent, TimerEventPost}

@RunWith(classOf[JUnitRunner])
class PersistorActorSpec extends Specification {

  "PersistorActor" should {
    "receive a TimerEventPost" in new WithApplication {
      val system = ActorSystem("test")
      val persistorActor = system.actorOf(Props[PersistorActor], name = "persistorActor")
      val result = persistorActor ! TimerEventPost("foo/bar", TimerEvent(20, DateTime.now()))
    }
  }
}
