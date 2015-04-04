package com.piranha

import akka.actor.Props
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.{ Application, GlobalSettings }

trait Global extends GlobalSettings {

}