package edu.nyu.dlts.mdi.fileident

import akka.actor.{ Actor, ActorSystem, Props }
import java.io.File
import Protocol._
import edu.nyu.dlts.mdi.fileident.actors._

object Main extends App {
	// initialize the actor system and log
	val system = ActorSystem("File-Identification")	

	//initialize supervisor
	val supervisor = system.actorOf(Props[Supervisor], "supervisor")
}
