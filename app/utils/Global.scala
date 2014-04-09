import play.api._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.info("Application has started, Demian was here")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown... Demian was here")
  }
}
