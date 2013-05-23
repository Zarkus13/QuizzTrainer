package models

import org.squeryl.Table
import DB._
import org.squeryl.dsl.OneToMany
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._
import play.api.libs.json._
import scala.util.Random

/**
 * Created with IntelliJ IDEA.
 * User: Alexis
 * Date: 22/05/13
 * Time: 02:50
 * To change this template use File | Settings | File Templates.
 */
case class Question(
       text: String,
       @Column("correct_answers") correctAnswers: String
) extends Model {
    type T = Question
    def table: Table[Question] = questions

    lazy val answers: OneToMany[Answer] = questionToAnswers.left(this)

    def writes(o: Model): JsValue = {
        transaction {
            Json.obj(
                "id" -> id,
                "text" -> text,
                "correctAnswers" -> correctAnswers,
                "answers" -> Json.arr(
                    Random.shuffle(answers.toList).map { a =>
                        a.toJson
                    }
                )
            )
        }
    }

    def toJson(): JsValue = {
        this.writes(this)
    }
}

object Question extends StaticModel {
    type T = Question
    def table: Table[Question] = questions

    def getAllIDs(): List[Long] = {
        inTransaction {
            from(questions)(q =>
                select(q.id)
            ).toList
        }
    }
}
