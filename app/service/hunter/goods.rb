require 'mongoid'

class Goods
  include Mongoid::Document
  field :asin
  field :title
  field :avgPrice, type: Integer
  field :minPrice, type: Integer
  field :repeat, type: Integer
  field :uri
end
