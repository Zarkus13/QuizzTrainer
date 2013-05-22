package utils

import play.api.mvc._

class ControllerWrapper extends Controller {
    implicit val msgs: Msgs = new Msgs

    implicit def flashingMsgs(msgs: Msgs): List[(String, String)] = {
        val msgsInfos: String = msgs.infos.mkString(":::")
        val msgsErrors: String = msgs.errors.mkString(":::")

        var list: List[(String, String)] = Nil

        msgs.infos match {
            case Nil => {}
            case i: List[String] => { list = list :+ ("msgs-infos" -> i.mkString(":::")) }
        }

        msgs.errors match {
            case Nil => {}
            case e: List[String] => { list = list :+ ("msgs-errors" -> e.mkString(":::")) }
        }

        list
    }

    def parseMsgsFromFlash[A](key: String, f: String => Unit)(implicit req: Request[A]): Unit = {
        flash.get(key) match {
            case Some(m) => m.split(":::") foreach f
            case None => {}
        }
    }

    def ActionWrapper[A](bodyParser: BodyParser[A] = parse.anyContent)(func: Request[A] => Result): Action[A] = {
        Action(bodyParser) { implicit req =>
            msgs.cleanAll()
            parseMsgsFromFlash("msgs-infos", msgs.addInfo)
            parseMsgsFromFlash("msgs-errors", msgs.addError)

            implicit val str: String = "toto"

            func(req)
        }
    }
}