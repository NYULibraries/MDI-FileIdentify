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

class Supervisor() extends Actor {
  implicit val timeout = new Timeout(5 seconds)

  def receive = {
  	case _ =>
  }
}