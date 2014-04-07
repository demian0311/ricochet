import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class GaugeSpec extends Specification {

  "Gauge" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "send 404 if path isn't known" in new WithApplication{
      val gaugeFooBar = route(FakeRequest(GET, "/gauge/foo/baz")).get
      status(gaugeFooBar) must equalTo(404)
      //status(home) must equalTo(OK)
      //contentType(home) must beSome.which(_ == "text/html")
      //contentAsString(home) must contain ("Your new application is ready.")
    }

    "send 201 on first POST" in new WithApplication{
      val response1 = route(FakeRequest(
        POST,
        "/gauge/foo/bar").withJsonBody(
          Json.obj("duration" -> 20))).get

      status(response1) must equalTo(201)
    }
  }
}