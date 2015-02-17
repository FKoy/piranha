namespace :hunter do

  desc "Run Crawler"
  task run: :environment do
    crawler = Hunter::Crawler.new
    crawler.run
  end

end
