# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://coffeescript.org/

ws_rails = new WebSocketRails "localhost:3000/websocket"

ws_rails.bind "websocket_bait", (bait) -> 
  bait_li = document.createElement("li");
  bait_li.textContent = bait;
  document.getElementById("list").appendChild(bait_li)

