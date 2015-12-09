package mdi.test

import com.rabbitmq.client._
import com.typesafe.config._
import java.io.File
import java.util.UUID
import org.scalatest._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class AMQPSpec extends FlatSpec with Matchers with AMQPTestSupport {
  
  "A RabbitMQ connection" should "have a heartbeat" in { getHeartbeat() >= 0 should be (true) }

  it should "have a channel id" in { getChannel() >= 0 should be (true) }

  "A RabbitMQ client" should "publish a file identification request and validate that a matching result is published" in {
    testFileIdentification() should be (true)
  }

}