package models

import org.squeryl.Table
import DB._

/**
 * Created with IntelliJ IDEA.
 * User: Alexis
 * Date: 22/05/13
 * Time: 02:50
 * To change this template use File | Settings | File Templates.
 */
case class Question(text: String, answers: List[Answer], correctAnswers: String) extends Model {
    type T = Question

    def table: Table[Question] = questions
}

object Question extends StaticModel {
    type T = Question

    def table: Table[Question] = questions
}
