package actors

import akka.actor.ActorSystem
import akka.actor.{Actor, Props}
import akka.event.Logging
import com.typesafe.config.ConfigFactory
import collection.JavaConversions._

class ScheduleActor extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case "start" => start
  }

  def start = {
    val config = ConfigFactory.load()
    val urls = config.getStringList("urls")

    val system = ActorSystem("CrawlSystem")
    for((url, index) <- urls.zipWithIndex){
      log.info("Start : "+url)
      val crawlActor = system.actorOf(Props(new CrawlActor), "CrawlActor" + index.toString)
      crawlActor ! url
    }
  }

}