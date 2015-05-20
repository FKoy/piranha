package actors

import models.{MailSender, Products, ProductUrls}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{NoSuchElementException, By}
import scala.collection.JavaConversions._
import akka.actor.Actor
import akka.event.Logging

class CrawlActor extends Actor {
  val log = Logging(context.system, this)
  val driver = new HtmlUnitDriver()

  def receive = {
    case url: String => scrap(url)
  }

  def scrap(rankingPageUrl: String) = {
    log.info("Start!")
    driver get rankingPageUrl
    try {
      val lastPage = fetchLastPageNumber(rankingPageUrl)

      for (page <- 1 to lastPage) {
        val url = rankingPageUrl + "&page=" + page.toString
        val productsLinks = extractAllProductsLinks(url)

        productsLinks.map(_.mkString).foreach(url => {
          try {
            driver get url

            val asin = fetchAsin(driver)
            val title = fetchTitle(driver)
            val imgSrc = fetchImgSrc(driver)

            val productUrl = new ProductUrls(asin, url)
            ProductUrls insert productUrl

            currentLowestPrice(driver, url) match {
              case Some(price: Int) => {

                val product = new Products(price, asin, title, imgSrc)
                if (Products priceHasChange product) {
                  log info "has changed!"
                  val mailSender = new MailSender(product)
                  mailSender.sendMail
                  Products insert product
                }

              }

              case None =>

            }
          } catch {
            case e: org.openqa.selenium.WebDriverException =>
          }
        })
      }
    } catch {
      case e: org.openqa.selenium.NoSuchElementException => log info rankingPageUrl
    }
  }

  def extractAllProductsLinks(url: String) = {
    driver.get(url)

    for (i <- 0 until 23) yield
      driver.findElementsByXPath("//*[@id=\"result_" + i.toString + "\"]/div/div[2]/div[1]/a")
        .map(_.getAttribute("href"))
  }

  def fetchLastPageNumber(url: String, elmNum: Int = 6): Int =
    try {
      driver.findElementByXPath(s"""//*[@id="pagn"]/span[${elmNum.toString}]""").getText.toInt
    } catch {
      case e: java.lang.NumberFormatException => fetchLastPageNumber(url, elmNum - 1)
    }

  def fetchTitle(driver: HtmlUnitDriver): String =
    driver.findElementById("productTitle").getText

  def fetchImgSrc(driver: HtmlUnitDriver): String =
    driver.findElementById("landingImage").getAttribute("src")

  def fetchAsin(driver: HtmlUnitDriver): String =
    driver.findElementById("ASIN").getAttribute("value")


  //FIXME otherPriceに送料が入っていない
  def currentLowestPrice(driver: HtmlUnitDriver, url: String): Option[Int] =
    (amazonPrice(driver), otherPrices(driver)) match {
      case (Some(x: Int), Some(y: List[Int])) => Some(comparePrices(x :: y))
      case (None, Some(y: List[Int])) => Some(comparePrices(y))
      case (Some(x: Int), None) => Some(x)
      case (None, None) => None
    }

  def amazonPrice(driver: HtmlUnitDriver) =
    try {
      Option(convertPriceFromStringToInt(driver findElementByXPath "//*[@id='priceblock_ourprice']" getText))
    } catch {
      case e: NoSuchElementException => None
    }

  def otherPrices(driver: HtmlUnitDriver) =
    try {
      Option(driver.findElementsByXPath("//*[@id=\"olp_feature_div\"]/div/span").map(price => {
        convertPriceFromStringToInt(price.findElement(By.tagName("span")).getText)
      }).toList)
    } catch {
      case e: NoSuchElementException => None
    }

  def convertPriceFromStringToInt(price: String): Int =
    price.split(" ")(1).replace(",", "").toInt

  def comparePrices(prices: List[Int]): Int = {
    var TheCheapestPrice = prices.head
    for (price <- prices.tail)
      if (price < TheCheapestPrice)
        TheCheapestPrice = price
    TheCheapestPrice
  }
}