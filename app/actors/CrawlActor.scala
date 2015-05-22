package actors

import models.{Products, MailSender, ProductUrl}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{WebElement, NoSuchElementException, By}
import collection.JavaConversions._
import akka.actor.Actor
import akka.event.Logging
import scala.util.{Success, Failure, Try}

class CrawlActor extends Actor {
  class ProductNotSuppliedException extends Exception
  class PageNotFoundException(message: String) extends Exception

  val log = Logging(context.system, this)
  val driver = new HtmlUnitDriver()

  def receive = {
    case url: String => scrap(url)
  }

  def scrap(url: String) =
    for ((pageUrl, pageNum) <- allPages(url, driver).zipWithIndex) {
      extractAllProductsLinks(pageUrl, pageNum).map(_.mkString).foreach(url => {
        saveProduct(driver, url)
      })
    }


  def allPages(rankingPageUrl: String, driver: HtmlUnitDriver): List[String] = {
    driver get rankingPageUrl
    fetchLastPageNumber(rankingPageUrl) match {
      case 0 => {
        log.info(s"Page not found ${rankingPageUrl}")
        List()
      }//throw new PageNotFoundException(s"Page not found ${rankingPageUrl}")
      case lastPage: Int =>
        (for (page <- 1 to lastPage) yield
          s"${rankingPageUrl}&page=${page.toString}").toList
    }
  }

  def fetchLastPageNumber(url: String, elmNum: Int = 6): Int =
    Try {
      if(elmNum == 0) return 0
      log.info(driver.findElementByXPath(s"""//*[@id="pagn"]/span[${elmNum.toString}]""").getText)
      driver.findElementByXPath(s"""//*[@id="pagn"]/span[${elmNum.toString}]""").getText.toInt
    } match {
      case Failure(e)=> fetchLastPageNumber(url, elmNum - 1)
      case Success(v)=> v
    }

  def extractAllProductsLinks(url: String, pageNum: Int) = {
    driver.get(url)
    for (i <- 0 until 23) yield
      driver.findElementsByXPath(s"""//*[@id="result_${(24*pageNum+i).toString}"]/div/div[2]/div[1]/a""")
        .map(_.getAttribute("href"))
  }

  def saveProduct(driver: HtmlUnitDriver, url: String) =
    currentLowestPrice(driver, url) match {
      case Some(price: Int) => {
        val asin = fetchAsin(driver)
        ProductUrl create (asin, url)
        val title = fetchTitle(driver)
        val imgSrc = fetchImgSrc(driver)
        Products create (price, asin, title, imgSrc)
      }
      case None => //throw new ProductNotSuppliedException
    }

  def fetchTitle(driver: HtmlUnitDriver): String =
    driver findElementById "productTitle" getText

  def fetchImgSrc(driver: HtmlUnitDriver): String =
    driver findElementById("landingImage") getAttribute("src")

  def fetchAsin(driver: HtmlUnitDriver): String =
    driver findElementById("ASIN") getAttribute("value")


  //FIXME otherPriceに送料が入っていない
  def currentLowestPrice(driver: HtmlUnitDriver, url: String): Option[Int] = {
    try{ driver get url }
    catch {case e: java.net.MalformedURLException => log.info(s"java.net.MalformedURLException:: ${url}")}
    comparePrices( amazonPrice(driver) :: otherPrices(driver))
  }

  def amazonPrice(driver: HtmlUnitDriver): Int =
    Try {
      driver.findElementByXPath("//*[@id='priceblock_ourprice']") getText
    } match {
      case Success(price: String) => convertPriceFromStringToInt(price)
      case Failure(e: NoSuchElementException) => -1
    }

  def otherPrices(driver: HtmlUnitDriver): List[Int] =
    Try { driver.findElementsByXPath("//*[@id=\"olp_feature_div\"]/div/span").toList }
    match {
      case Success(list: List[WebElement]) =>
        list.map(elm => {
          Try { convertPriceFromStringToInt(elm.findElement(By.tagName("span")).getText) }
          match {
            case Success(price: Int) => price
            case Failure(e: NoSuchElementException) => -1
          }
        })
      case Failure(e: NoSuchElementException) => List()
    }

  def comparePrices(prices: List[Int]): Option[Int] = {
    val filtered = prices.filter(_ > 0)
    if (filtered.isEmpty)
      None
    else
      Some(filtered.min)
  }

  def convertPriceFromStringToInt(price: String): Int = price.split(" ")(1).replace(",", "").toInt

}