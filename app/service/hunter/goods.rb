require 'mongoid'

class Goods
  include Mongoid::Document
  field :cntPrice, type: Integer
  field :asin
  field :avgPrice, type: Integer
  field :minPrice, type: Integer
  field :repeat, type: Integer
  field :uri
end
