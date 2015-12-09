package mdi.test

import akka.actor.{ ActorSystem, Props, Actor, ActorRef }
import _root_.akka.pattern.ask
import java.io.File
import akka.pattern.ask
import akka.util.Timeout
import edu.nyu.dlts.mdi.fileident.AMQPSupport
import edu.nyu.dlts.mdi.fileident.Protocol._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future, Await }
import scala.concurrent._
import scala.language.postfixOps

import TestProtocol._ 

trait AMQPTestSupport {
  implicit val timeout = new Timeout(5 seconds)

  val system = ActorSystem("File-Identification-Test")
  val publisher = system.actorOf(Props[TestPublisher], "TEST_PUBLISHER")	
  
  def getHeartbeat(): Int = {
  	val future = publisher ? Heartbeat
  	Await.result(future, timeout.duration) match {
  	  case i: Int => i
  	  case _ => -1	
  	}
    //0
  }
}


class TestPublisher() extends Actor with AMQPSupport {
  val connection = getConnection.get
  val connections = getAMQPConnections(connection).get
  implicit val formats = DefaultFormats

  def receive = {	
  	case p: Publish => {
  	  connections.publisher.basicPublish(conf.getString("rabbitmq.exchange"), conf.getString("rabbittest.test_request_key"), null, p.message.getBytes())
  	}

  	case Heartbeat => sender ! connection.getHeartbeat()
  	
  	case _ => 
  }
}