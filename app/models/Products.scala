package models

import com.mongodb.casbah.Imports._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.Logger

case class Products(price: Int,
                    asin: String,
                    title: String,
                    imgSrc: String,
                    created_at: String = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new DateTime())) {
  def toMongoDBObject =
    MongoDBObject(
      "price" -> price,
      "asin" -> asin,
      "created_at" -> created_at
    )

  def priceLowest_? : Boolean =
    if (price <= Products.lowestPrice(this)) true else false

  def priceLowerThanAverage_? : Boolean =
    if (price < Products.averagePrice(this)) true else false
}

object Products {
  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("piranha")
  val collection = db("products")


  def create(price: Int, asin: String, title: String, imgSrc: String) = {
    val product = new Products(price, asin, title, imgSrc)
    if (priceHasChange(product)) {
      Logger.info("hasChanged")
      val mailSender = new MailSender(product)
      mailSender.deliver
      collection insert product.toMongoDBObject
    }
    product
  }

  def priceHasChange(product: Products): Boolean =
    mostRecentPrice(product) match {
      case 0 => true
      case price: Int => if(price != product.price) true else false
    }

  def mostRecentPrice(product: Products): Int =
    collection.findOne(
      MongoDBObject("asin" -> product.asin),
      MongoDBObject("price" -> 1),
      MongoDBObject("created_at" -> -1)
    ) match {
      case Some(p) => {
        p.getAs[Int]("price") getOrElse 0
      }
      case None => 0
    }

  def lowestPrice(product: Products): Int = {
    collection.findOne(
      MongoDBObject("asin" -> product.asin),
      MongoDBObject(),
      MongoDBObject("price" -> 1)
    ) match {
      case Some(p) => p.getAs[Int]("price") getOrElse 0
      case None => 0
    }
  }
  /*
  def beginningSales_?(product: Products) : Boolean = {

  }
*/
  def averagePrice(product: Products): Int = {
    var (sum, count) = (0, 0)
    collection find MongoDBObject( "asin" -> product.asin) foreach( o => {
      sum += o.get("price").toString.toInt
      count += 1
    })

    count match {
      case 0 => 0
      case _ => sum / count
    }
  }
}