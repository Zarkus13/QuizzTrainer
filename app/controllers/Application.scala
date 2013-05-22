package controllers

import play.api._
import play.api.mvc._
import utils.ControllerWrapper

object Application extends ControllerWrapper {
  
  def index = ActionWrapper(parse.anyContent) { implicit req =>
    Ok(views.html.index("Your new application is ready."))
  }
  
}