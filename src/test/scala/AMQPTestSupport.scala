package mdi.test

import akka.actor.{ ActorSystem, Props, Actor, ActorRef }
import com.typesafe.config._
import java.io.File
import java.util.UUID
import akka.pattern.ask
import akka.util.Timeout
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future, Await }
import scala.concurrent._
import scala.language.postfixOps
import scala.sys.process._
import org.json4s.DefaultFormats._

import edu.nyu.dlts.mdi.fileident.{ FidoSupport, AMQPSupport }
import edu.nyu.dlts.mdi.fileident.Protocol._

import TestProtocol._

trait AMQPTestSupport extends FidoSupport with AMQPConfiguration {
  
  implicit val timeout = new Timeout(5 seconds)

  val system = ActorSystem("File-Identification-Test") 

  val vectorManager = system.actorOf(Props[VectorManager], "Vector_Manager")

  val publisher = system.actorOf(Props[TestPublisher], "TEST_PUBLISHER")	
  
  val consumerProps = Props(new TestConsumer(vectorManager))
  val consumer = system.actorOf(consumerProps, "TEST_CONSUMER")

  consumer ! Listen
  
  def getHeartbeat(): Int = {
  	val future = publisher ? GetHeartbeat
  	
    Await.result(future, timeout.duration) match {
  	  case i: Int => i
  	  case _ => -1	
  	}
  }

  def testFile(): Boolean = {
    val file = new File(conf.getString("rabbittest.test_file_loc"))
    file.exists
  }

  def testFido(): Boolean = {
    var fido = ""
    val process = ProcessLogger((o: String) => fido = o)
    Seq("which", "fido2") ! process
    fido != ""
  }

  def getChannel(): Int = {
    val future = publisher ? GetChannel
    
    Await.result(future, timeout.duration) match {
      case i: Int => i
      case _ => -1  
    }
  }

  def testFileIdentification(): Boolean = {
    val id = UUID.randomUUID.toString
    val json = compact(render(( 
      ("version" -> "0.0.1") ~ 
      ("request_id" -> id) ~ 
      ("params" -> ("request_path" -> conf.getString("rabbittest.test_file_loc")))
    )))
   
    publisher ! TestRequest(json)
    
    Thread.sleep(5000)

    val future = vectorManager ? new IdRequest(id)

    Await.result(future, timeout.duration) match {
      case i: Boolean => i
      case _ => false
    }

  }
}

class VectorManager extends Actor {

  implicit val formats = DefaultFormats

  var map: Map[String, String]  = Map()
  def receive = {
    case Dump => println(map)
    
    case p: Publish => { 
      val id = (parse(p.message) \ "request_id").extract[String]
      map = map + (id ->  p.message) 
    }

    case i:IdRequest => sender ! map.contains(i.uuid) 


    case _ =>
  }

}

trait AMQPConfiguration { val conf = ConfigFactory.load() }

class TestPublisher() extends Actor with AMQPSupport with AMQPConfiguration {
  val publisher = getPublisher(conf.getString("rabbitmq.host"))
  implicit val formats = DefaultFormats

  def receive = {	
  	case request: TestRequest => {
   
  	   publisher.basicPublish(conf.getString("rabbitmq.exchange_name"), conf.getString("rabbitmq.publish_request_key"), null, request.message.getBytes())
  	}

    case response: TestResponse => {
       publisher.basicPublish(conf.getString("rabbitmq.exchange_name"), conf.getString("rabbitmq.publish_response_key"), null, response.message.getBytes())
    }

  	case GetHeartbeat => sender ! publisher.getConnection.getHeartbeat()
  	
    case GetChannel => sender ! publisher.getChannelNumber()
  	
    case _ => 
  }
}

class TestConsumer(vec: ActorRef) extends Actor with AMQPSupport with AMQPConfiguration {
  
  import edu.nyu.dlts.mdi.fileident.actors.Identifier 

  val consumer = getConsumer(conf.getString("rabbitmq.host"), conf.getString("rabbitmq.exchange_name"), conf.getString("rabbitmq.consume_key"))

  val identifierProps = Props(new Identifier(vec))
  val identifier = context.actorOf(identifierProps, "IDENTIFIER")
  
  implicit val formats = DefaultFormats

  def receive = {
    case Listen => {
      val delivery = consumer.nextDelivery()
      val message = new String(delivery.getBody())
      val json = parse(message)
      val request_id = UUID.fromString((json \ "request_id").extract[String])
      val request_path = ((json \ "params") \ "request_path").extract[String]
      val request = new FileIdentRequest(request_id, new File(request_path))
      identifier ! request 
      self ! Listen 
    }

    case _ =>
  }
}
