package org.denigma.nlp

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.typesafe.config.Config
import org.scalatest.concurrent.Futures
import org.scalatest.{BeforeAndAfterAll, Inside, Matchers, WordSpec}

import scala.concurrent.duration._
/**
  * Created by antonkulaga on 31/03/16.
  */
class BasicSuite extends WordSpec with Matchers with ScalatestRouteTest with Futures with Inside with BeforeAndAfterAll{

  implicit val duration: FiniteDuration = 1 second

  implicit val timeout:Timeout = Timeout(duration)
}
class BasicKappaSuite extends BasicSuite {

  val config: Config = system.settings.config

}