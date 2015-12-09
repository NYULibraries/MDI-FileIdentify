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

class PublishSpec extends FlatSpec with Matchers with AMQPTestSupport {
  
  "A RabbitMQ connection" should "have a heartbeat" in {
    getHeartbeat() >= 0 should be (true)
  }
/*
  val connection = getConnection.get
  val channel = connection.createChannel()
  val exchangeName = conf.getString("rabbitmq.exchange")
  channel.exchangeDeclare(exchangeName, "topic")
  val queueName = channel.queueDeclare().getQueue()
  val testKey = conf.getString("rabbittest.test_request_key")
  channel.queueBind(queueName, exchangeName, testKey) 

  "A RabbittMQ channel " should "have a channel number" in {
    val channelNumber = channel.getChannelNumber()
    channelNumber >= 0 should be (true)
  }

  it should "be able to be published to" in {
    val json = ( 
      ("version" -> "0.0.1") ~ 
      ("request_id" -> UUID.randomUUID.toString) ~ 
      ("params" -> ("request_path" -> conf.getString("rabbittest.test_file_loc")))
    ) 

    channel.basicPublish(exchangeName, testKey, null, compact(render(json)).getBytes()) 
    1 should be (1)
  }
  */
  
}