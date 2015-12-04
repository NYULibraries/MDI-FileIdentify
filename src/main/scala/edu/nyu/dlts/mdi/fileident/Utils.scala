package edu.nyu.dlts.mdi.fileident

import org.joda.time._
import org.joda.time.format.ISODateTimeFormat
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import edu.nyu.dlts.mdi.fileident.Protocol._

trait SysUtils {
	
  def now(): String = {
    val dt = new DateTime()
    val fmt = ISODateTimeFormat.dateTime()
    fmt.print(dt)
  }

  def createResult(response: Response): String = {
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
			("data" -> null) 
		)

		compact(render(json))

	} 

	def getAgent(): Agent = {
		import scala.sys.process._
		val command = Seq("ewfinfo", "-V")
		var version = "NO_VERSION"
		var agent = "NO_AGENT"

		val ewfVersion = "^ewfinfo.*".r
		
		val logger = ProcessLogger( 
			(o: String) => { 
				o match {
					case ewfVersion(_*) => { 
						agent = o.split(" ")(0)
						version = o.split(" ")(1)
					}
					case _ =>
				} 
			})
		
		command ! logger

		new Agent(agent, version, "localhost")
	}
}