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
        val currentQuestion: String = "0"

        println("questionsIDs : " + questionsIDs)

        Redirect(routes.Quizz.quizz()).withSession(
            "questionsIDs" -> questionsIDs,
            "currentQuestion" -> currentQuestion
        )
    }

    def quizz() = ActionWrapper(parse.anyContent) { implicit req =>
        val questionsIDs: String = req.session.get("questionsIDs").getOrElse(
            Random.shuffle(Question.getAllIDs()).mkString(",")
        )
        val currentQuestion: String = req.session.get("currentQuestion").getOrElse("0")

        println("quizz questionsID : " + questionsIDs)

        Ok(views.html.quizz(questionsIDs, currentQuestion.toLong)).withSession(
            "questionsIDs" -> questionsIDs,
            "currentQuestion" -> String.valueOf(currentQuestion)
        )
    }

    def question(id: Long) = ActionWrapper(parse.anyContent) { implicit req =>
        Question.findById(id).map({ q =>
//            val currentQuestion: Int = req.session.get("currentQuestion").map({
//                s => s.toInt
//            }).getOrElse(0)

            println("questionsIDs from session : " + req.session.get("questionsIDs").get)
            val questionsIDs: Option[Array[Int]] = req.session.get("questionsIDs").map({ s =>
                Some(s.split(",").map(i => i.toInt))
            }).getOrElse(
                None
            )

            println("questionsIDs : " + questionsIDs.get)
            println("id question : " + id)

            val currentQuestion: Int = questionsIDs match {
                case None => 0
                case ids: Some[Array[Int]] => {
                    ids.get.indexOf(id)
                }
            }

            println("currentQuestion : " + currentQuestion)

            Ok(q.toJson()).withSession(
                session +
                ("currentQuestion" -> String.valueOf(currentQuestion))
            )
        }).getOrElse(
            NotFound("Question with id " + id + " not found").withSession(session)
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
                Ok(Json.obj("success" -> answers)).withSession(session)
            else
                Ok(Json.obj("fail" -> q.correctAnswers)).withSession(session)
        }).getOrElse(
            NotFound("Question with id " + id + " not found").withSession(session)
        )
    }
}
