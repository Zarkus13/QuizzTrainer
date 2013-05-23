package models

import org.squeryl.Table
import DB._
import org.squeryl.dsl.ManyToOne
import org.squeryl.annotations._
import play.api.libs.json.{Json, JsValue, Writes}

/**
 * Created with IntelliJ IDEA.
 * User: Alexis
 * Date: 22/05/13
 * Time: 02:54
 * To change this template use File | Settings | File Templates.
 */
case class Answer(
         letter: String,
         text: String,
         @Column("question_fk") questionId: Option[Long]
) extends Model {
    type T = Answer
    def table: Table[Answer] = answers

    lazy val question: ManyToOne[Question] = questionToAnswers.right(this)

    def writes(o: Model): JsValue = {
        Json.obj(
            "id" -> id,
            "letter" -> letter,
            "text" -> text,
            "questionId" -> questionId
        )
    }

    def toJson(): JsValue = {
        writes(this)
    }
}

object Answer extends StaticModel {
    type T = Answer
    def table: Table[Answer] = answers
}