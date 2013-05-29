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
            "id" -> longNumber(min = 0),
            "answers" -> nonEmptyText,
            "currentQuestion" -> longNumber(min = 0)
        )
    )

    def beginQuizz() = SecuredAction {
        ActionWrapper(parse.anyContent) { implicit req =>
            val questionsIDs: String = Random.shuffle(Question.getAllIDs()).mkString(",")

            println("questionsIDs : " + questionsIDs)

            Redirect(routes.Quizz.quizz).withSession(
                session +
                ("questionsIDs" -> questionsIDs) +
                ("currentQuestion" -> "0") +
                ("nbPassed" -> "0") +
                ("nbCorrect" -> "0")
            )
        }
    }

    def quizz() = SecuredAction {
        ActionWrapper(parse.anyContent) { implicit req =>
            val questionsIDs: String = req.session.get("questionsIDs").getOrElse(
                Random.shuffle(Question.getAllIDs()).mkString(",")
            )
            val currentQuestion: String = req.session.get("currentQuestion").getOrElse("0")
            val nbPassed: String = req.session.get("nbPassed").getOrElse("0")
            val nbCorrect: String = req.session.get("nbCorrect").getOrElse("0")

            println("quizz questionsID : " + questionsIDs)

            Ok(views.html.quizz(questionsIDs, currentQuestion.toInt, nbPassed.toInt, nbCorrect.toInt)).withSession(
                session +
                ("questionsIDs" -> questionsIDs) +
                ("currentQuestion" -> String.valueOf(currentQuestion)) +
                ("nbPassed" -> nbPassed) +
                ("nbCorrect" -> nbCorrect)
            )
        }
    }

    def question(id: Long) = SecuredAction {
        ActionWrapper(parse.anyContent) { implicit req =>
            Question.findById(id).map({ q =>

                val questionsIDs: Option[Array[Int]] = req.session.get("questionsIDs").map({ s =>
                    Some(s.split(",").map(i => i.toInt))
                }).getOrElse(
                    None
                )

                val currentQuestion: Int = questionsIDs match {
                    case None => 0
                    case ids: Some[Array[Int]] => {
                        ids.get.indexOf(id)
                    }
                }

                Ok(q.toJson()).withSession(
                    session +
                    ("currentQuestion" -> String.valueOf(currentQuestion))
                )
            }).getOrElse(
                NotFound("Question with id " + id + " not found").withSession(session)
            )
        }
    }

    def answerQuestion() = SecuredAction {
        ActionWrapper(parse.anyContent) { implicit req =>
            answerForm.bindFromRequest().fold(
                errors => BadRequest,
                params => {
                    val id = params._1
                    val answers = params._2
                    val currentQuestion = params._3 + 1

                    Question.findById(id).map({ q =>
                        val ans1 = q.correctAnswers.split(",").distinct.sortBy(s => s)
                        val ans2 = answers.split(",").distinct.sortBy(s => s)

                        val test: Boolean = ans1.corresponds(ans2) {
                            (a, b) => a == b
                        }

                        println("Question " + id + " : correct answers = " + ans1.mkString(",") + " - your answers : " + ans2.mkString(",") + " - Correct ? " + test)

                        val nbPassed = req.session.get("nbPassed").map({ s => s.toInt }).getOrElse(0)
                        val nbCorrect = req.session.get("nbCorrect").map({ s => s.toInt }).getOrElse(0)

                        if(test)
                            Ok(
                                Json.obj(
                                    "success" -> answers,
                                    "nbPassed" -> String.valueOf(nbPassed + 1),
                                    "nbCorrect" -> String.valueOf(nbCorrect + 1)
                                )
                            ).withSession(
                                session +
                                ("nbPassed" -> String.valueOf(nbPassed + 1)) +
                                ("nbCorrect" -> String.valueOf(nbCorrect + 1)) +
                                ("currentQuestion" -> String.valueOf(currentQuestion))
                            )
                        else
                            Ok(
                                Json.obj(
                                    "fail" -> q.correctAnswers,
                                    "nbPassed" -> String.valueOf(nbPassed + 1),
                                    "nbCorrect" -> String.valueOf(nbCorrect)
                                )
                            ).withSession(
                                session +
                                ("nbPassed" -> String.valueOf(nbPassed + 1)) +
                                ("currentQuestion" -> String.valueOf(currentQuestion))
                            )
                    }).getOrElse(
                        NotFound("Question with id " + id + " not found").withSession(session)
                    )
                }
            )
        }
    }

    def score() = SecuredAction {
        ActionWrapper(parse.anyContent) { implicit req =>
            val nbPassed = req.session.get("nbPassed").map({ s => s.toInt }).getOrElse(0)
            val nbCorrect = req.session.get("nbCorrect").map({ s => s.toInt }).getOrElse(0)
            val score = if(nbPassed != 0) (nbCorrect * 100 / nbPassed) else 0

            println("nbPassed = " + nbPassed + " - nbCorrect = " + nbCorrect + " - score = " + score)

            Ok(views.html.score(score)).withSession(session)
        }
    }
}
