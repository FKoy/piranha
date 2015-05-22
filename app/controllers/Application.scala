package controllers

import models.Products
import play.api.cache.Cache
import play.api.libs.iteratee.{Enumerator, Concurrent, Iteratee}
import play.api.libs.json.JsValue
import play.api.mvc._
import play.{Logger, api}
import play.api.libs.concurrent.Execution.Implicits._
import scala.collection.mutable._
import scala.util.{Success, Failure, Try}

object Application extends Controller {

  def index = Action { request =>
    Ok(views.html.index(request))
  }

  var channel: MutableList[Concurrent.Channel[String]] = MutableList()

  def ws = WebSocket.using[String] { request =>
    Logger.info(cacheRead.toString)
    val log = Try{"{\"init\":["+cacheRead.reduceLeft((a: String, b: String) => s"${a},${b}")+"]}"} match {
      case Failure(e: UnsupportedOperationException) => ""
      case Success(log: String) => log
    }

    val out = Concurrent.unicast[String] { c =>{
        c.push(log)
        channel += c
      }
    }
    val in = Iteratee.ignore[String]

    (in, out)
  }

  def productInfoReciever = Action { request =>
    val info: String = request.body.asJson.getOrElse("").toString
    channel.foreach(_.push(info))
    cacheWrite(info)

    Ok("success!")
  }

  import play.api.Play.current

  private[this] def cacheRead = Cache.getAs[List[String]]("information.log").getOrElse(List())
  private[this] def cacheWrite(info: String) = Cache.set("information.log", info :: cacheRead, 216000)

}