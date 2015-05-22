self = @

this.startWebSocket = (webSocketUrl) ->
  connection = new WebSocket(webSocketUrl)
  connection.onopen = (e) ->
  connection.onerror = (e) ->
  connection.onmessage = (e) ->
    try
      console.log(e)
      updateInformation(JSON.parse(e.data))
    catch
      for info in JSON.parse(e.data).init.reverse()
        updateInformation(info)

updateInformation = (product) ->
  newItem = createNewItemTag(product)
  information = document.getElementById('information')
  information.insertBefore(newItem, information.children[0])

setEventInit = ->
  triggers = document.getElementsByClassName('trigger')
  [].forEach.call triggers, (trigger) ->
    setTriggerEvent(trigger)

setTriggerEvent = (triggerTag) ->
  triggerTag.addEventListener 'click', ->
    asin = triggerTag.getAttribute("asin")
    self.shown.style.display = 'none' if self.shown
    self.shown = document.getElementsByClassName(asin)[0]
    self.shown.style.display = 'block'

createNewItemTag = (product) ->
  imgTag = createImgTag product
  titleTag = createTitleTag product
  avgPriceTag = createAvgPrice product
  currentPriceTag = createCurrentPrice product
  minPriceTag = createMinPrice product
  triggerTag = createTriggerTag(product)
  link = createLink(product)
  foundAt = createFoundAtTag
  wrapperTag = createWrapperTag(product, imgTag, titleTag, currentPriceTag, avgPriceTag, minPriceTag, link, foundAt)
  item = document.createElement('li')
  item.appendChild(triggerTag)
  item.appendChild(wrapperTag)
  item

createImgTag = (product) ->
  img = document.createElement('img')
  imgSrc = document.createAttribute('src')
  imgSrc.value = product.img_src
  imgClass = document.createAttribute('class')
  imgClass.value = "product_img"
  imgWidth = document.createAttribute('width')
  imgWidth.value = 200
  img.setAttributeNode(imgSrc)
  img.setAttributeNode(imgClass)
  img

createTitleTag = (product) ->
  title = document.createElement('div')
  titleClass = document.createAttribute('class')
  titleClass.value = "title"
  title.setAttributeNode(titleClass)
  title

createCurrentPrice = (product) ->
  currentPrice = document.createElement('div')
  currentPriceClass = document.createAttribute('class')
  currentPriceClass.value = "current-price"
  currentPrice.textContent = 'current : '+formatData2Price(product.current_price)
  currentPrice.setAttributeNode(currentPriceClass)
  currentPrice

createMinPrice = (product) ->
  minPrice = document.createElement('div')
  minPriceClass = document.createAttribute('class')
  minPriceClass.value = "min-price"
  minPrice.textContent = 'min : '+formatData2Price(product.min_price)
  minPrice.setAttributeNode(minPriceClass)
  minPrice

createAvgPrice = (product) ->
  avgPrice = document.createElement('div')
  avgPriceClass = document.createAttribute('class')
  avgPriceClass.value = "avg-price"
  avgPrice.textContent = 'avg : '+formatData2Price(product.avg_price)
  avgPrice.setAttributeNode(avgPriceClass)
  avgPrice

createTriggerTag = (product) ->
  trigger = document.createElement('a')
  triggerClass = document.createAttribute('class')
  triggerClass.value = "trigger"
  trigger.setAttributeNode(triggerClass)
  triggerAsin = document.createAttribute('asin')
  triggerAsin.value = product.asin
  trigger.setAttributeNode(triggerAsin)
  trigger.innerText = triggerText(product)
  setTriggerEvent(trigger)
  trigger

triggerText = (product) ->
  "#{product.title.substr(0, 50)} #{product.current_price} #{product.min_price} #{product.avg_price}"

createWrapperTag = (product, img, title, currentPrice, avgPrice, minPrice, link) ->
  wrapper = document.createElement('div')
  wrapperClass = document.createAttribute('class')
  wrapperClass.value = "wrapper"
  wrapper.setAttributeNode(wrapperClass)
  wrapperAsin = document.createAttribute('class')
  wrapperAsin.value = product.asin
  wrapper.setAttributeNode(wrapperAsin)
  wrapper.appendChild(img)
  wrapper.appendChild(title)
  wrapper.appendChild(currentPrice)
  wrapper.appendChild(avgPrice)
  wrapper.appendChild(minPrice)
  wrapper.appendChild(link)
  wrapper.style.display = 'none'
  wrapper

createLink = (product) ->
  link = document.createElement('a')
  linkClass = document.createAttribute('class')
  linkClass.value = "url"
  link.setAttributeNode(linkClass)
  linkHref = document.createAttribute('href')
  linkHref.value = product.url
  link.setAttributeNode(linkHref)
  link.textContent = product.url
  link

createFoundAtTag = (product) ->
  foundAt = document.createElement('div')
  foundAtClass = document.createAttribute('class')
  foundAtClass.value = "found-at"
  foundAt.textContent = product.found_at
  foundAt.setAttributeNode(foundAtClass)

formatData2Price = (price) ->
  formatted = ''
  for c,i in price.toString().split('').reverse()
    formatted += if i % 3 is 0 then ',' + c else c
  formatted.slice(1).split('').reverse().join('')