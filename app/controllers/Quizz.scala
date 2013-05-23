package controllers

import utils.ControllerWrapper
import models.Question
import scala.util.Random
import play.api.libs.json.Json

/**
 * Created with IntelliJ IDEA.
 * User: Alexis
 * Date: 22/05/13
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
object Quizz extends ControllerWrapper {

    def beginQuizz() = ActionWrapper(parse.anyContent) { implicit req =>

        val questionsIDs: String = req.session.get("questions")
            .getOrElse(
                Random.shuffle(Question.getAllIDs()).mkString(",")
            )

        val currentQuestion: Int = req.session.get("currentQuestion").map({
            id => id.toInt + (if(id.toInt >= questionsIDs.split(",").size - 1) 0 else 1)
        }).getOrElse(0)

        println("questionsIDs : " + questionsIDs)

        Ok(views.html.quizz(questionsIDs, currentQuestion)).withSession(
            "questions" -> questionsIDs,
            "currentQuestion" -> String.valueOf(currentQuestion)
        )
    }

    def question(id: Long) = ActionWrapper(parse.anyContent) { implicit req =>
        Question.findById(id).map({ q =>
            Ok(q.toJson())
        }).getOrElse(
            NotFound("Question with id " + id + " not found")
        )
    }

    def answerQuestion(id: Long, answers: String) = ActionWrapper(parse.anyContent) { implicit req =>
        if(id <= -1 || answers == null) BadRequest

        Question.findById(id).map({ q =>
            val ans1 = q.correctAnswers.split(",").distinct.sortBy(s => s)
            val ans2 = answers.split(",").distinct.sortBy(s => s)

            var test: Boolean = ans1.corresponds(ans2) {
                (a, b) => a == b
            }

            if(test)
                Ok(Json.obj("success" -> ("Correct " + answers)))
            else
                Ok(Json.obj("fail" -> ("Incorrect ! Correct answer is : " + q.correctAnswers)))
        }).getOrElse(
            NotFound("Question with id " + id + " not found")
        )
    }
}
