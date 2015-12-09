package edu.nyu.dlts.mdi.fileident

import java.util.UUID
import org.joda.time._
import org.joda.time.format.ISODateTimeFormat
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import edu.nyu.dlts.mdi.fileident.Protocol._

trait CommonUtils {
	
  def now(): String = {
    val dt = new DateTime()
    val fmt = ISODateTimeFormat.dateTime()
    fmt.print(dt)
  }
  
  def createNewResponse(): Response = { new Response("0.1", UUID.randomUUID(), None, now(), None, getAgent(), None) }

  def convertResponseToJson(response: Response): String = {
		val json = ( 
			("version" -> response.version) ~ 
			("request_id" -> response.request_id.toString()) ~ 
			("outcome" -> response.outcome.getOrElse(null))  ~ 
			("start_time" -> response.start_time) ~ 
			("end_time" -> response.end_time.getOrElse(null)) ~
			("agent" -> (
				("name" -> response.agent.agent) ~ 
				("version" -> response.agent.version) ~ 
				("host" -> response.agent.host))) ~
			("data" -> response.data) 
		)

		compact(render(json))
	} 

	def getAgent(): Agent = {
		import scala.sys.process._
		
		var fidoVersion = ""
      	val versionLogger = ProcessLogger((o: String) => fidoVersion = fidoVersion + o)
      	Seq("/usr/local/bin/fido", "-v") ! versionLogger

      	var hostname = ""
      	var hostProcess = ProcessLogger((o: String) => hostname = o)
      	Seq("hostname") ! hostProcess

      	
		val versFields = fidoVersion.toString().split(" ")
		
		new Agent("fido", versFields(1).substring(1), hostname)
	}
}