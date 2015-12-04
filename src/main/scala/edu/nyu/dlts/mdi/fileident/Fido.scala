package edu.nyu.dlts.mdi.fileident

import java.io.File
import scala.sys.process._
import scala.util.matching.Regex._
import edu.nyu.dlts.mdi.fileident.Protocol._

trait FidoSupport {

  val pattern = "^\".*\"$".r

  def getFido(file: File): Option[FidoResponse] = {
    try {
      
      var fidoIdent = ""
      val identLogger = ProcessLogger((o: String) => fidoIdent = fidoIdent ++ o)
      Seq("/usr/local/bin/fido", file.getAbsolutePath) ! identLogger

      var fidoVersion = ""
      val versionLogger = ProcessLogger((o: String) => fidoVersion = fidoVersion ++ o)
      Seq("/usr/local/bin/fido", "-v") ! versionLogger

      val pronomFields = fidoIdent.toString().split(",")
      val versFields = fidoVersion.toString().split(" ")


      Some(
        new FidoResponse(
          removeQuotes(pronomFields(0)), 
          removeQuotes(pronomFields(2)), 
          removeQuotes(pronomFields(3)), 
          removeQuotes(pronomFields(4)), 
          removeQuotes(pronomFields(7)), 
          removeQuotes(pronomFields(8)),
          versFields(2).substring(1, versFields(2).length - 1), 
          versFields(3).substring(0, versFields(3).length - 1), 
          versFields(1).substring(1)
        )
      )
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