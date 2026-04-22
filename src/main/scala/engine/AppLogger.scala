package engine

import java.util.logging.{Logger, FileHandler, SimpleFormatter, Level}
import java.io.File

object AppLogger {

  val logger: Logger = {

    System.setProperty(
      "java.util.logging.SimpleFormatter.format",
      "%1$tF %1$tT %4$s %5$s%n"
    )
    new File("logs").mkdirs()

    val loggerInstance = Logger.getLogger("Order Transaction Logger")

    // Info logs
    val infoHandler = new FileHandler("logs/pipeline.log", true)
    infoHandler.setFormatter(new SimpleFormatter())
    infoHandler.setLevel(Level.INFO)

    // Error logs
    val errorHandler = new FileHandler("logs/errors.log", true)
    errorHandler.setFormatter(new SimpleFormatter())
    errorHandler.setLevel(Level.SEVERE)

    
    loggerInstance.addHandler(infoHandler)
    loggerInstance.addHandler(errorHandler)
    
    loggerInstance.setLevel(Level.ALL)

    loggerInstance
  }
}