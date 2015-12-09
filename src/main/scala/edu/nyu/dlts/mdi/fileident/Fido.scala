package edu.nyu.dlts.mdi.fileident

import java.io.File
import scala.sys.process._
import scala.util.matching.Regex._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

import edu.nyu.dlts.mdi.fileident.Protocol._

trait FidoSupport {


  def getFido(file: File): Option[JObject] = {
    var fido: Option[String] = Some("")

    val ok = "^OK.*".r

    val iProcess = ProcessLogger((o: String) => {
      ok.findFirstIn(o) match {
        case Some(i) => fido = Some(o)
        case None =>  
      }
    })

    Seq("/usr/local/bin/fido", file.getAbsolutePath) ! iProcess

    var fidoVersion = ""
    val version = ProcessLogger((o: String) => fidoVersion = fidoVersion + o)
    Seq("/usr/local/bin/fido", "-v") ! version


   

    fido match {

      case Some(f) => {
        val pronomFields = f.split(",")
        val versFields = version.toString().split(" ")
        val json = (
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
      }
      case None => None
    }
  }

  def removeQuotes(in: String): String = {

    if(("^\".*\"$".r findAllIn in).isEmpty){
      in
    } else {
      in.substring(1, in.length -1)
    }
  }
}