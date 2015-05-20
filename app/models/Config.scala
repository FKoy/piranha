package models

import com.typesafe.config.ConfigFactory
import play.api.Configuration

/**
 * Created by koya on 15/05/13.
 */
object Config {
  def load = ConfigFactory.load()
}
