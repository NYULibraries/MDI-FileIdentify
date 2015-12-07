package edu.nyu.dlts.mdi.fileident

import java.io.File
import java.util.UUID
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
  	data: Option[String]
  )

  case class AMQPConnections(consumer: QueueingConsumer, publisher: Channel)
  case class FileIdentRequest(id: UUID, file: File)
  case class FidoResponse(result: String, puid: String, format: String, sigName: String, mime: String, matchType: String, sigFile: String, contFile: String, fidoVers: String)
  case class Publish(message: String)
  
  case object Listen
}