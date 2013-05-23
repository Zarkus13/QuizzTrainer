package controllers

import utils.ControllerWrapper
import models.Question
import scala.util.Random

/**
 * Created with IntelliJ IDEA.
 * User: Alexis
 * Date: 22/05/13
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
object Quizz extends ControllerWrapper {

    def beginQuizz() = ActionWrapper(parse.anyContent) { implicit req =>

        val questionsIDs: List[Long] = req.session.get("questions").map({ ids =>
            ids.toList.map(id => id.toLong)
        }).getOrElse(Question.getAllIDs())

//        val questionsIDs: List[Long] = Question.getAllIDs()

        Ok(views.html.quizz(questionsIDs)).withSession(
            "questions" -> questionsIDs.mkString(",")
        )
    }
}
