package models

import play.api.libs.json.{Json, JsValue, Writes}

/**
 * Created by koya on 15/05/13.
 */
class ProductInfo(product: Products) {

  val price: Int = product.price
  val asin: String = product.asin
  val minPrice: Int = Products lowestPrice product
  val avgPrice: Int = Products averagePrice product
  val title: String = product.title
  val imgSrc: String = product.imgSrc
  val url: String = ProductUrls.getByAsin(product.asin)
  val created_at = product.created_at

  implicit val productInfoWrites = new Writes[ProductInfo] {
    def writes(productInfo: ProductInfo): JsValue =
      Json.obj(
        "current_price" -> productInfo.price,
        "asin" -> productInfo.asin,
        "min_price" -> productInfo.minPrice,
        "avg_price" -> productInfo.avgPrice,
        "title" -> productInfo.title,
        "img_src" -> productInfo.imgSrc,
        "url" -> productInfo.url,
        "found_at" -> productInfo.created_at
      )
  }

  def toJson = Json.toJson(this)
}
