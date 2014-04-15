import actors.PersistorActor
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import org.joda.time.DateTime
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import akka.pattern.ask

import play.api.test._
import play.mvc.Http.Response
import scala.concurrent.Future
import scala.util.{Failure, Success}
import utils.{TimerEventRequest, TimerEvent, TimerEventPost}

import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class PersistorActorSpec extends Specification {

  "PersistorActor" should {
    "receive a TimerEventPost" in new WithApplication {
      val system = ActorSystem("test")
      val persistorActor = system.actorOf(Props[PersistorActor], name = "persistorActor")
      val result = persistorActor ! TimerEventPost("foo/bar", TimerEvent(20, DateTime.now()))
    }


    "respond to a good query with a 200" in new WithApplication {
      val system = ActorSystem("test")
      val persistorActor = system.actorOf(Props[PersistorActor], name = "persistorActor")

      val timerEventRequest: TimerEventRequest = TimerEventRequest("foo/bar/respond/to/query")


      implicit val timeout = Timeout(5000)
      val actorResponse: Future[Any] = persistorActor ? timerEventRequest

      Thread.sleep(500);

      /*
      val simpleResponse: Response = actorResponse.onComplete{
        case Success(theValue) => theValue
        case Failure(theFailure) => theFailure
      }*/

      Thread.sleep(500)
    }
  }
}
