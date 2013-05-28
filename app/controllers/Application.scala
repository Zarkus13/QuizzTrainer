package controllers

import play.api._
import play.api.mvc._
import utils.ControllerWrapper
import models.{Answer, Question}
import org.squeryl.PrimitiveTypeMode._

object Application extends ControllerWrapper {

    def index = SecuredAction {
        ActionWrapper(parse.anyContent) { implicit req =>

            Ok(views.html.index("Your new application is ready.")).withSession(session)
        }
    }

    def discardSession = SecuredAction {
        ActionWrapper(parse.anyContent) { implicit req =>

            Ok(views.html.index("Session cleaned")).withNewSession
        }
    }
}