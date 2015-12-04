package edu.nyu.dlts.mdi.fileident

import java.io.File
import java.util.UUID

object Protocol {
  
  case class Agent(
  	agent: String,
  	version: String,
  	host: String
  )

  case class Request(
  	version: String,
  	request_id: UUID,
    file: File,
  	outcome: Option[String],
  	start_time: String,
  	end_time: Option[String],
  	agent: Agent,
  	data: Option[String]
  )
}