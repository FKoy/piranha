package models

import com.mongodb.casbah.Imports._

case class ProductUrls(asin: String,
                    url: String)

object ProductUrls {
  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("piranha")
  val collection = db("product_urls")

  def insert(productUrl: ProductUrls) = {
    if(collection.find(MongoDBObject("asin" -> productUrl.asin)).isEmpty) {
      collection.insert(
        MongoDBObject(
          "asin" -> productUrl.asin,
          "url" -> productUrl.url
        )
      )
    }
  }

  def getByAsin(asin: String): String = {
    collection.findOne(MongoDBObject("asin" -> asin)).get("url").toString
  }
}