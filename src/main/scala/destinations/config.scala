package destinations

import java.io.File
import java.net.URL
import java.util.*
import scala.io.Source

object config {

  private val propertiesPath = new File(".").getCanonicalPath + "/resources/application.properties"
  def getConfig: Either[Throwable, Properties] =
    val properties: Properties = new Properties()

    try
      val source = Source.fromFile(propertiesPath)
      properties.load(source.bufferedReader())
      Right(properties)
    catch
      case e: Throwable => Left(e)
}
