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
import scala.sys.process._

class AMQPSpec extends FlatSpec with Matchers with AMQPTestSupport {
  
  "A test file" should "exist" in {
  	assert(testFile() == true)
  }

  "The fido command" should "be available on the system" in {
  	assert(testFido == true)
  }

  "A RabbitMQ connection" should "have a heartbeat" in { getHeartbeat() >= 0 should be (true) }

  it should "have a channel id" in { getChannel() >= 0 should be (true) }

  "A RabbitMQ client" should "publish a file identification request and validate that a matching result is published" in {
    testFileIdentification() should be (true)
  }

}