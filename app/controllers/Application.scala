package controllers

import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import scala.collection.mutable._

object Application extends Controller {

  def index = Action { request =>
    Ok(views.html.index(request))
  }

  var channel: MutableList[Concurrent.Channel[String]] = MutableList()

  def ws = WebSocket.using[String] { request =>
    val in = Iteratee.ignore[String]
    val out = Concurrent.unicast[String]{ c => channel += c }
    (in, out)
  }

  def productInfoReciever = Action { request =>
    channel.foreach(_.push(request.body.asJson.getOrElse("").toString))
    Ok("success!")
  }

}