package edu.nyu.dlts.mdi.fileident

import java.io.File
import java.util.UUID
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import com.rabbitmq.client.{ QueueingConsumer, Channel }

object Protocol {
  
  case class Agent(
  	agent: String,
  	version: String,
  	host: String
  )

  case class Response(
  	version: String,
  	request_id: UUID,
  	outcome: Option[String],
  	start_time: String,
  	end_time: Option[String],
  	agent: Agent,
  	data: Option[JObject]
  )

  case class AMQPConnections(consumer: QueueingConsumer, publisher: Channel)
  case class FileIdentRequest(id: UUID, file: File)
  
  case class Publish(message: String)
  
  case object Listen
}