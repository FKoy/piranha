class Goods
  include Mongoid::Document
  field :asin
  field :avgPrice, type: Integer
  field :minPrice, type: Integer
  field :cntPrice, type: Integer
  field :repeat, type: Integer
end
