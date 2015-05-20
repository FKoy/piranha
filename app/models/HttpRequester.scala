package models

import play.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.Play.current

/**
 * Created by koya on 15/05/13.
 */
class HttpRequester(url: String) {
  def post(product: Products) = {
    val info = new ProductInfo(product)
    WS.url(url+"/product/info").withHeaders("Content-Type" -> "application/json").post(Json.stringify(info.toJson))
  }
}

object HttpRequester {
  def apply(url: String): HttpRequester =
    new HttpRequester(url)
}