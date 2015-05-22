package models

import com.mongodb.casbah.Imports._

case class ProductUrl(asin: String, url: String)

object ProductUrl {
  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("piranha")
  val collection = db("product_urls")

  def create(asin: String, url: String): ProductUrl = {
    val productUrl =  collection.findOne(MongoDBObject("asin" -> asin))
     productUrl match {
      case Some(productUrl: ProductUrl.collection.T) =>
      case None => collection.insert(MongoDBObject("asin" -> asin, "url" -> url))
    }
    new ProductUrl(asin, url)
  }

  def getByAsin(asin: String): String =
    collection.findOne(MongoDBObject("asin" -> asin)).get("url").toString
}