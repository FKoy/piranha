/**
 * Created by koya on 15/04/27.
 */
package models

import org.apache.commons.mail.SimpleEmail
import collection.JavaConversions._
import play.api.Logger

class MailSender(product: Products) {
  val config = Config.load

  def deliver = {
    noticeProduct2Client
    /*if (Products beginningSales_? product) {
      createMail('sale)
    } else*/ if (product priceLowest_?) {
      createMail('min)
    } else if (product priceLowerThanAverage_?) {
      createMail('avg)
    }
  }

  private def createMail(contentType: Symbol) = {
    val recipients = config getStringList "recipients"

    val mail = new Mail(product, contentType)
    for(recipient <- recipients) {
      mail.send(recipient)
    }
  }

  private def noticeProduct2Client :Unit = {
    val url = config.getString("system.url")
    val httpRequester = new HttpRequester(url)
    Logger.info("Found!!"+ product.toString)
    httpRequester post product
  }
}

class Mail(product: Products, contentType: Symbol) {
  val avgPrice = Products averagePrice product
  val minPrice = Products lowestPrice product
  val htmlText = s"asin: ${product.asin}\navg: ${avgPrice}\nmin: ${minPrice}\nnow: ${product.price}"
  val subtitle = contentType match {
    case 'avg => "Found reasonable product! It's price lower than average."
    case 'min => "Found reasonable product! It's The Cheapest price!!!"
  }

  def send(recipient: String) = {
    new SimpleEmail {
      setHostName("smtp.gmail.com")
      setFrom("fukushi678@gmail.com")
      addTo(recipient)
      setSubject(subtitle)
      setMsg(htmlText)
      setSmtpPort(465)
      setAuthentication("fukushi678","fukushi123")
      setSSL(true)
    }.send
  }
}
