class WebsocketGateController < WebsocketRails::BaseController

  def pass

    bait = data()

    broadcast_message(:predation, bait);

  end

end
