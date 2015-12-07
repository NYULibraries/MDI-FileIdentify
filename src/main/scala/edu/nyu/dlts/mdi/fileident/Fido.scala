package edu.nyu.dlts.mdi.fileident

import java.io.File
import scala.sys.process._
import scala.util.matching.Regex._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

import edu.nyu.dlts.mdi.fileident.Protocol._

trait FidoSupport {

  val pattern = "^\".*\"$".r

  def getFido(file: File): Option[JObject] = {
    try {
      
      var fidoIdent = ""
      val identLogger = ProcessLogger((o: String) => fidoIdent = fidoIdent + (o + "\n"))
      Seq("/usr/local/bin/fido", file.getAbsolutePath) ! identLogger

      var fidoVersion = ""
      val versionLogger = ProcessLogger((o: String) => fidoVersion = fidoVersion + o)
      Seq("/usr/local/bin/fido", "-v") ! versionLogger


      val fidoOutput = fidoIdent.split("\n")
      val pronomFields = fidoOutput(1).split(",")
      val versFields = fidoVersion.toString().split(" ")

      val json = (
        //format: String, sigName: String, mime: String, matchType: String, sigFile: String, contFile: String, fidoVers: String)
        ("result" -> pronomFields(0)) ~
        ("puid" -> pronomFields(2)) ~
        ("format" -> removeQuotes(pronomFields(3))) ~
        ("signame" -> removeQuotes(pronomFields(4))) ~
        ("mime" -> removeQuotes(pronomFields(7))) ~
        ("matchType" -> removeQuotes(pronomFields(8))) ~
        ("sigFile" -> versFields(2).substring(1, versFields(2).length - 1)) ~
        ("contFile" -> versFields(3).substring(0, versFields(3).length - 1)) 

      )
      
      Some(json)

    } catch {
      case e: Exception => { println(e); None  }
    }
  }

  def removeQuotes(in: String): String = {
    if((pattern findAllIn in).isEmpty){
      in
    } else {
      in.substring(1, in.length -1)
    }
  }

}