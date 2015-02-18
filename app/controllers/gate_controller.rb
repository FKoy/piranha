class GateController < ApplicationController
  protect_from_forgery :except => [:pass]
  def pass
    logger.debug(params)
    oprice = params[:oprice]
    nprice = params[:nprice]
    asin   = params[:asin]
    url    = params[:url]
    mprice = params[:mprice]
    render :nothing => true
  end
end
