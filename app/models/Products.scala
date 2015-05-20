package models

import com.mongodb.casbah.Imports._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

case class Products(price: Int,
                    asin: String,
                    title: String,
                    imgSrc: String,
                    created_at: String = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new DateTime())) {
  def toMongoDBObject = {
    MongoDBObject(
      "price" -> price,
      "asin" -> asin,
      "created_at" -> created_at
    )
  }
}

object Products {
  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("piranha")
  val collection = db("products")

  def insert(product: Products) = {
    val mostRecentPrice =
      collection.findOne(
        MongoDBObject("asin" -> product.asin),
        MongoDBObject("price" -> 1),
        MongoDBObject("created_at" -> -1)
      )

    mostRecentPrice.getOrElse(None) match {
      case price: com.mongodb.BasicDBObject =>
        if(priceHasChange(product))
          collection.insert( product.toMongoDBObject )
      case None =>
        collection.insert( product.toMongoDBObject )
    }
  }

  def priceHasChange(product: Products): Boolean = {
    val mostRecentPrice =
      collection.findOne(
        MongoDBObject("asin" -> product.asin),
        MongoDBObject("price" -> 1),
        MongoDBObject("created_at" -> -1)
      )

    mostRecentPrice.getOrElse(None) match {
      case price: com.mongodb.BasicDBObject =>
        if(price.get("price") != product.price) true else false
      case None => {
        true
      }
    }
  }

  def mostRecentPrice(product: Products) =
    collection.findOne(
      MongoDBObject("asin" -> product.asin),
      MongoDBObject("price" -> 1),
      MongoDBObject("created_at" -> -1)
    )

  def priceLowest_?(product: Products): Boolean = {
    if (product.price <= lowestPrice(product)) true else false
  }

  def priceLowerThanAverage_?(product: Products): Boolean = {
    if (product.price < averagePrice(product)) true else false
  }

  def lowestPrice(product: Products): Int = {
    val price = collection.findOne(
                  MongoDBObject("asin" -> product.asin),
                  MongoDBObject(),
                  MongoDBObject("price" -> 1)
                )

    price match {
      case None => 0
      case Some(p: collection.T) => p.get("price").toString.toInt
    }
  }
  /*
  def beginningSales_?(product: Products) : Boolean = {

  }
*/
  def averagePrice(product: Products): Int = {
    var sum = 0
    var count = 0

    collection.find( MongoDBObject("asin" -> product.asin) ) foreach(o => {
      sum += o.get("price").toString.toInt
      count += 1
    })

    count match {
      case 0 => 0
      case _ => sum / count
    }
  }
}