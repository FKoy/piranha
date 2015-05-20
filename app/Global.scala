import actors.ScheduleActor
import akka.actor.Props
import play.api.{Application, GlobalSettings}
import play.api.Logger
import akka.actor.ActorSystem

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    val system = ActorSystem("ScheduleSystem")
    val crawler = system.actorOf(Props[ScheduleActor], "ScheduleActor")
    crawler ! "start"
    //QuartzSchedulerExtension(system).schedule("Every3Minutes", crawler, "start")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

}
