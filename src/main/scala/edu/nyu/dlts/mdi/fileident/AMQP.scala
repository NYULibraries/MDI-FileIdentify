package edu.nyu.dlts.mdi.fileident

import edu.nyu.dlts.mdi.fileident.Protocol._

import com.typesafe.config._
import com.rabbitmq.client._
import java.util.UUID
import java.io.File

trait AMQPSupport {

  val factory = new ConnectionFactory

  def getConsumer(host: String, exhangeName: String, consumeKey: String): QueueingConsumer = {
    val connection = getConnection(host)
    val channel = getChannel(connection)
    val queueName = channel.queueDeclare().getQueue()
    channel.queueBind(queueName, exhangeName, consumeKey)
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume(queueName, true, consumer)
    consumer
  }

  def getPublisher(host: String): Channel = {
    val connection = getConnection(host)
    getChannel(connection)
  }

  private def getConnection(host: String): Connection = {
    factory.setHost(host)
    factory.newConnection()
  }

  private def getChannel(connection: Connection): Channel = {
    val channel = connection.createChannel()
    channel.queueDeclare().getQueue()
    channel
  }

}