package models

import org.squeryl.Table
import DB._

/**
 * Created with IntelliJ IDEA.
 * User: Alexis
 * Date: 22/05/13
 * Time: 02:54
 * To change this template use File | Settings | File Templates.
 */
case class Answer(letter: String, text: String, question: Question) extends Model {
    type T = Answer

    def table: Table[Answer] = answers
}

object Answer extends StaticModel {
    type T = Answer

    def table: Table[Answer.T] = answers
}