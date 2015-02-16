class WebsocketReceiveController < WebsocketRails::BaseController

  def recieve

    recieve_data = data()

    broadcast_message(:websocket_bait, recieve_data);

  end

end
