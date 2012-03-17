import play.api.GlobalSettings
import org.squeryl._
import play.api.db.DB
import play.api.Play.current
import org.squeryl.adapters.H2Adapter

object Global extends GlobalSettings {

	override def onStart(app: play.api.Application) {
		SessionFactory.concreteFactory = Some(() =>
			Session.create(DB.getConnection(), new H2Adapter)
		)
		super.onStart(app)
	}

}