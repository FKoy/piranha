require 'mongoid'
require 'open-uri'
require 'nokogiri'
require './goods.rb'

module Hunter
  class Crawler
    @@URL = 'http://www.amazon.co.jp/gp/bestsellers/'
    @@CATEGORIES = [
      'fashion',
      'diy',
      'dvd',
      'toys',
      'automotive',
      'videogames',
      'beauty',
      'shoes',
      'jewelry',
      'sports',
      'software',
      'computers',
      'hpc',
      'baby',
      'pet-supplies',
      'hobby',
      'kitchen',
      'appliances',
      'electronics',
      'office-products',
      'apparel',
      'books',
      'musical-instruments',
      'english-books',
      'watch',
      'food-beverage'
    ]
    def initialize
    end
    def readUrl(url)
      charset = nil
      begin
        html = open(url.strip) do |f|
          charset = f.charset
          f.read
        end
      rescue OpenURI::HTTPError
        doc = nil
      rescue URI::InvalidURIError
        doc = nil
      rescue Zlib::BufError
      end
      doc = Nokogiri::HTML.parse(html, nil, charset)
      return doc
    end

    def fetchPrice(loc)
      doc = readUrl(loc)
      amazonPrice = nil
      if doc.css('#priceblock_ourprice') == nil then
        amazonPrice = convertFormat(doc.css('#priceblock_saleprice').text).to_i
      else
        amazonPrice = convertFormat(doc.css('#priceblock_ourprice').text).to_i
      end

      newGoodsPrice = nil
      unless doc.css('#olp_feature_div > div > span > span') == nil then
        newGoodsPrice = convertFormat(doc.css('#olp_feature_div > div > span > span').text).to_i
      end

      oldGoodsPrice = nil
      unless doc.css('#olp_feature_div > div > span:nth-child(2) > span') == nil then
        oldGoodsPrice = convertFormat(doc.css('#olp_feature_div > div > span:nth-child(2) > span').text).to_i
      end
      asin = loc.split('/')[-1]
      return asin, min(amazonPrice, newGoodsPrice, oldGoodsPrice)
    end

    def min(amazon, new, old)
      amazon = nil if !amazon.nonzero?
      new    = nil if !new.nonzero?
      old    = nil if !old.nonzero?
      min = nil
      for price in [amazon, new, old] do
        min = price if min == nil or (price != nil and price < min)
      end
      return min
    end

    def convertFormat(str)
      str = str.gsub(",", "")
      str = str.split(" ")[1]
      return str
    end

    def update(asin, newPrice, loc)
      goods = Goods.where(:asin => asin).first
      newPrice = newPrice.to_i
      if goods == nil then
        goods = Goods.new
        goods.avgPrice = newPrice
        goods.minPrice = newPrice
        goods.repeat = 1
        goods.asin = asin
        goods.uri = loc
        goods.save
        return newPrice
      end

      oldPrice = goods.cntPrice
      minPrice = goods.minPrice

      if oldPrice == nil or newPrice < minPrice then
        notice minPrice, oldPrice, newPrice, asin, loc
        oldPrice = newPrice
        goods.minPrice = newPrice
      elsif newPrice < goods.cntPrice then
        notice minPrice, oldPrice, newPrice, asin, loc
      end

      goods.cntPrice = newPrice
      goods.repeat += 1
      goods.avgPrice = oldPrice - (oldPrice - newPrice) / goods.repeat
      goods.cntPrice = newPrice
      goods.save

      return newPrice
    end

    def notice(minPrice, avgPrice, newPrice, asin, url)
      params = {
        'aprice' => avgPrice,
        'nprice' => newPrice,
        'asin'   => asin,
        'url'    => url,
        'mprice' => minPrice
      }
      url = URI.parse('http://localhost:3000/gate/notice')
      response = Net::HTTP.post_form(url, params)
      puts response
    end

    def run
      Mongoid.load!("config/mongoid.yml", :development)
      @@CATEGORIES.each do |category|
        doc = readUrl(@@URL+category)
        doc.css('.zg_title a').each do |node|
          goodsLoc = node.attribute('href').value.strip
          begin
            asin, price = fetchPrice(goodsLoc)
          rescue OpenURI::HTTPError => error
            next
          rescue URI::InvalidURIError => error
            next
          end
          if price == nil then
            next
          end
          puts asin + ':' + price.to_s
          update(asin, price, goodsLoc)
        end
      end
    end
  end
end
