package edu.nyu.dlts.mdi.fileident

import akka.actor.{ ActorSystem, Props }
import java.io.File
import Protocol._
import edu.nyu.dlts.mdi.fileident.actors._

object Main extends App with TestRequestSupport {
	// initialize the actor system and log
	val system = ActorSystem("File-Identification")	

	//initialize supervisor
	val supervisor = system.actorOf(Props[Supervisor], "supervisor")

	testRequest()
}