package engine
import AppLogger.logger
import java.time.LocalDateTime
import scala.io.Source
import scala.collection.parallel.{ForkJoinTaskSupport}
import scala.collection.parallel.CollectionConverters._
import scala.util.Using

object Main extends App {

  val startTime = System.nanoTime()
  val batchSize = 500000
  val forkJoinPool = new java.util.concurrent.ForkJoinPool(4)


  logger.info("Pipeline started ")

  try {
    logger.info("Phase 1: Loading file started ")

    val conn = DB_connection.getConnection()
    conn.setAutoCommit(false)

    try {

      Using.resource(Source.fromFile("src/main/resources/TRX10M.csv")) { source =>

        logger.info("File loaded successfully")

        logger.info("Phase 2: Processing batches started ")

        source.getLines()
          .drop(1)
          .grouped(batchSize)
          .zipWithIndex
          .foreach { case (batch, index) =>

            logger.info(s"Processing batch ${index + 1}")

            val parBatch = batch.par
            parBatch.tasksupport = new ForkJoinTaskSupport(forkJoinPool)

            val processedBatch = parBatch.map { line =>
              val order = utils.parseLine(line)

              val discount = Rules.CalculateDiscount(order)
              val total = order.unitPrice * order.quantity
              val finalPrice = total * (1 - discount)

              ProcessedOrder(
                transactionDate = order.transactionDate,
                quantity = order.quantity,
                productName = order.productName,
                discount = discount,
                unitPrice = order.unitPrice,
                finalPrice = finalPrice,
                processedAt = LocalDateTime.now()
              )
            }.toList

            logger.info(s"Batch ${index + 1} processed  (${processedBatch.size} records)")

            logger.info(s"Saving batch ${index + 1} to DB ")

            DB.saveBatch(conn, processedBatch) match {
              case Right(_) =>
                conn.commit()
                logger.info(s"Batch ${index + 1} saved successfully ✔")

              case Left(err) =>
                conn.rollback()
                logger.severe(s"Batch ${index + 1} failed : $err")
            }
          }
      }

    } finally {
      conn.close()
      logger.info("DB connection closed ")
    }

  } catch {
    case e: Exception =>
      logger.severe(s"Fatal pipeline error : ${e.getMessage}")
  }

  val endTime = System.nanoTime()
  val durationSeconds = (endTime - startTime) / 1e9

  logger.info("Pipeline finished ")
  logger.info(f"Total processing time: $durationSeconds%.2f seconds")
}