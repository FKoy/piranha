namespace :hunter do

  desc "Run Crawler"
  task run: do
    crawler = Hunter::Crawler.new
    crawler.run
  end

end
