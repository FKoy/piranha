class WebsocketGateController < WebsocketRails::BaseController
  def predation(bait)
    broadcast_message(:predation, bait)
  end
  def connect(bait)
    logger.debug(bait)
  end
end
