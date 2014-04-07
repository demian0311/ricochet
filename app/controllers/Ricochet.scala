package controllers

import play.api.mvc.{Controller, Action}
import play.api._

class Ricochet extends Controller{

  def index = Action { request =>
    Ok("Got request [" + request + "]")
  }

}
