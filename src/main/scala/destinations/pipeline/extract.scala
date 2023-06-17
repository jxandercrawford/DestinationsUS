package destinations.pipeline

import java.io.{File, FileInputStream, FileOutputStream, InputStream}
import java.net.URL
import java.nio.file.Path
import java.util.zip.ZipInputStream
import scala.language.postfixOps
import scala.sys.process.*

object extract {

  def downloadFromURL(urlToDownload: String, downloadDestination: String): Unit =
    new URL(urlToDownload) #> new File(downloadDestination) !!

  def deleteFile(fileToDelete: String): Unit =
    File(fileToDelete).delete()
    ()

  def unzipFile(zipFile: String, destination: String): Unit =
    // REF: https://stackoverflow.com/questions/30640627/how-to-unzip-a-zip-file-using-scala
    val zis = new ZipInputStream(FileInputStream(File(zipFile)))

    LazyList.continually(zis.getNextEntry).takeWhile(_ != null).foreach { file =>
      if !file.isDirectory then
        val outPath = Path.of(destination).resolve(file.getName)
        val outPathParent = outPath.getParent
        if !outPathParent.toFile.exists() then
          outPathParent.toFile.mkdirs()

        val outFile = outPath.toFile
        val out = new FileOutputStream(outFile)
        val buffer = new Array[Byte](4096)
        LazyList.continually(zis.read(buffer)).takeWhile(_ != -1).foreach(out.write(buffer, 0, _))
    }

  def unzipFile(zipFileToUnzip: String, destinationToUnloadContents: String, fileNamePatternToDownload: String): Unit =
    // REF: https://stackoverflow.com/questions/30640627/how-to-unzip-a-zip-file-using-scala
    val zis = new ZipInputStream(FileInputStream(File(zipFileToUnzip)))

    LazyList.continually(zis.getNextEntry).takeWhile(_ != null).foreach { file =>
      if !file.isDirectory then
        val outPath = Path.of(destinationToUnloadContents).resolve(file.getName)
        val outPathParent = outPath.getParent
        if !outPathParent.toFile.exists() then
          outPathParent.toFile.mkdirs()

        val outFile = outPath.toFile
        if outFile.getName.matches(fileNamePatternToDownload) then
          val out = new FileOutputStream(outFile)
          val buffer = new Array[Byte](4096)
          LazyList.continually(zis.read(buffer)).takeWhile(_ != -1).foreach(out.write(buffer, 0, _))
    }
}
