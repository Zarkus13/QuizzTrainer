package controllers

import utils.ControllerWrapper
import play.api.data.Form
import play.api.data.Forms._

/**
 * Created with IntelliJ IDEA.
 * User: alexis
 * Date: 28/05/13
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
object Login extends ControllerWrapper {

    val authForm = Form(
        tuple(
            "username" -> nonEmptyText,
            "password" -> nonEmptyText
        )
    )

    def login() = ActionWrapper(parse.anyContent) { implicit req =>
        Ok(views.html.login(authForm)).withSession(session)
    }

    def authentication() = ActionWrapper(parse.anyContent) { implicit req =>
        authForm.bindFromRequest.fold(
            errors => BadRequest(views.html.login(errors)),
            success => {
                if(success._1 == "patapouet" && success._2 == "pancaketerrible") {
                    msgs.addInfo("Connexion réussie")

                    Redirect(routes.Application.index())
                        .flashing(msgs:_*)
                        .withSession(session + ("username" -> success._1))
                }
                else {
                    msgs.addError("Couple utilisateur/mot de passe incorrecte")

                    BadRequest(views.html.login(authForm.fill(success))).withSession(session)
                }
            }
        )
    }
}
