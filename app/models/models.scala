/**
 * Created with IntelliJ IDEA.
 * User: alexis
 * Date: 19/03/13
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
package models

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json.{JsValue, Writes}

trait Model extends KeyedEntity[Long] with Writes[Model] {
    type T <: Model
    def table: Table[T]
    val id: Long = 0

    def save(): Unit = {
        inTransaction {
            table.insertOrUpdate(this.asInstanceOf[T])
        }
    }

    def delete(): Unit = {
        inTransaction {
            table.delete(id)
        }
    }

    def toJson(): JsValue
}

trait StaticModel {
    type T <: Model
    def table: Table[T]

    def all: List[T] = {
        inTransaction {
            from(table)(i => select(i)).toList
        }
    }

    def findById(id: Long): Option[T] = {
        inTransaction {
            table.lookup(id)
        }
    }

    def deleteById(id: Long): Boolean = {
        inTransaction {
            table.delete(id)
        }
    }
}

object DB extends Schema {
    val questions = table[Question]("questions")
    val answers = table[Answer]("answers")

    // RELATIONS
    val questionToAnswers = oneToManyRelation(questions, answers)
        .via((q, a) => q.id === a.questionId)
}