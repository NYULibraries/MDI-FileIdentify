package edu.nyu.dlts.mdi.fileident.actors

import akka.actor.{ Actor, ActorRef, Props, PoisonPill }
import akka.pattern.ask
import akka.util.Timeout
import java.io.{ File, FileWriter }
import java.util.UUID
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import com.rabbitmq.client.{ Channel, Connection }
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.io.Source
import scala.language.postfixOps
import org.joda.time._
import org.joda.time.format.ISODateTimeFormat

import edu.nyu.dlts.mdi.fileident.Protocol._
import edu.nyu.dlts.mdi.fileident.{ FidoSupport, AMQPSupport, CommonUtils }


class Supervisor() extends Actor {
  implicit val timeout = new Timeout(5 seconds)

  val consumerProps = Props(new Consumer(self))
  val consumer = context.actorOf(consumerProps, "Consumer")

  consumer ! Listen

  val identifierProps = Props(new Identifier(self))
  val identifier = context.actorOf(identifierProps, "Identifier")

  def receive = {
  
  	case fir: FileIdentRequest => identifier ! fir 
  	case _ =>
  }
}

class Consumer(supervisor: ActorRef) extends Actor with AMQPSupport {
  val connection = getConnection.get
  val connections = getAMQPConnections(connection).get
  implicit val formats = DefaultFormats

  def receive = {

  	case Listen => {
 	    val delivery = connections.consumer.nextDelivery()
      val message = new String(delivery.getBody())
      val json = parse(message)
      val request_id = UUID.fromString((json \ "request_id").extract[String])
      val request_path = ((json \ "params") \ "request_path").extract[String]
 
 	    //do something with a message
      supervisor ! new FileIdentRequest(request_id, new File(request_path))
	    
      self ! Listen 
  	}

  	case _ => 
  }
}

class Identifier(supervisor: ActorRef) extends Actor with FidoSupport with CommonUtils {
  def receive = {	
	case fir: FileIdentRequest => {

    var response = createNewResponse

    getFido(fir.file) match {
      case fido: Some[JObject] => {
        response = response.copy(outcome = Some("success"), end_time = Some(now()), data = fido )
        println(convertResponseToJson(response))
      }

      case None => 
    }
	}
  }
}

class Publisher(supervisor: ActorRef) extends Actor with AMQPSupport {
  val connection = getConnection.get
  val connections = getAMQPConnections(connection).get
  implicit val formats = DefaultFormats

  def receive = {	
  	case p: Publish => {
  		connections.publisher.basicPublish(conf.getString("rabbitmq.exchange"), conf.getString("rabbitmq.publish_key"), null, p.message.getBytes())
  	}
  	case _ => println("Message Not Understood")
  }
}