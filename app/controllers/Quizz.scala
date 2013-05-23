package controllers

import utils.ControllerWrapper
import models.Question
import scala.util.Random
import play.api.libs.json.Json
import play.api.data._
import play.api.data.Forms._

/**
 * Created with IntelliJ IDEA.
 * User: Alexis
 * Date: 22/05/13
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
object Quizz extends ControllerWrapper {

    val answerForm = Form(
        tuple(
            "id" -> longNumber,
            "answers" -> text
        )
    )

    def beginQuizz() = ActionWrapper(parse.anyContent) { implicit req =>
        val questionsIDs: String = Random.shuffle(Question.getAllIDs()).mkString(",")
        val currentQuestion: Int = 0

        println("questionsIDs : " + questionsIDs)

        Ok(views.html.quizz(questionsIDs, currentQuestion)).withSession(
            "questions" -> questionsIDs
        )
    }

    def question(id: Long) = ActionWrapper(parse.anyContent) { implicit req =>
        Question.findById(id).map({ q =>
            Ok(q.toJson())
        }).getOrElse(
            NotFound("Question with id " + id + " not found")
        )
    }

    def answerQuestion() = ActionWrapper(parse.anyContent) { implicit req =>
        val params = answerForm.bindFromRequest().get
        val id = params._1
        val answers = params._2

        if(id <= -1 || answers == null) BadRequest

        Question.findById(id).map({ q =>
            val ans1 = q.correctAnswers.split(",").distinct.sortBy(s => s)
            val ans2 = answers.split(",").distinct.sortBy(s => s)

            var test: Boolean = ans1.corresponds(ans2) {
                (a, b) => a == b
            }

            if(test)
                Ok(Json.obj("success" -> answers))
            else
                Ok(Json.obj("fail" -> q.correctAnswers))
        }).getOrElse(
            NotFound("Question with id " + id + " not found")
        )
    }
}
