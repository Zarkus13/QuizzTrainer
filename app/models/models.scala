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

trait Model extends KeyedEntity[Long] {
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
    val questions = table[Question]("QUESTIONS")
    val answers = table[Answer]("ANSWERS")
//    val emails = table[Email]("EMAILS")
//    val skymemLinks = table[SkymemLink]("SKYMEM_LINKS")
}