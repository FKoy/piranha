require 'test_helper'

class GateControllerTest < ActionController::TestCase
  test "should get path" do
    get :path
    assert_response :success
  end

end
