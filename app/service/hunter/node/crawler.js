mongoose = require 'mongoose'
client   = require 'cheerio-httpcli'

class Crawler
  @URL : 'http://www.amazon.co.jp/gp/bestsellers/'
  @CATEGORIES : [
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
  readUrl : (@url) ->
    client.fetch(@url, function (err, $, res) {
      if err
        
      $('a').each(function (idx) {
        console.log($(this).attr('href'));
      });
    });

