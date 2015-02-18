class GateController < ApplicationController
  protect_from_forgery :except => [:pass]
  def pass
    logger.debug(WebsocketGateController)
    bait = {
      'oprice' => params[:oprice],
      'nprice' => params[:nprice],
      'asin'   => params[:asin],
      'url'    => params[:url],
      'mprice' => params[:mprice]
    }
    predation bait.to_s
    render :nothing => true
  end
end
