import play.api.{Play, Application, GlobalSettings}
import org.squeryl._
import org.squeryl.adapters.MySQLAdapter
import play.api.db.DB

object Global extends GlobalSettings {

    override def onStart(app: Application) {

        println("Super global settings start")

        Class.forName("com.mysql.jdbc.Driver")
        SessionFactory.concreteFactory = Some(() => Session.create(
            DB.getConnection()(app),
            new MySQLAdapter)
        )
    }
}
