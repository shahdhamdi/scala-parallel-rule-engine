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

  logger.info("Pipeline started")

  try {

    DB_connection.getConnection() match {

      case Right(conn) =>

        // disable auto commit for batch control
        conn.setAutoCommit(false)

        try {

          Using.resource(Source.fromFile("src/main/resources/TRX10M.csv")) { source =>

            logger.info("Processing started")

            source.getLines()
              .drop(1) // skip header
              .grouped(batchSize)
              .zipWithIndex
              .foreach { case (batch, index) =>

                logger.info(s"Processing batch ${index + 1}")

                // parallel processing
                val parBatch = batch.par
                parBatch.tasksupport = new ForkJoinTaskSupport(forkJoinPool)

                val processedBatch = parBatch.map { line =>

                  val order = utils.parseLine(line)

                  val discount = Rules.CalculateDiscount(order)
                  val total = order.unitPrice * order.quantity
                  val finalPrice = total * (1 - discount)

                  ProcessedOrder(
                    order.transactionDate,
                    order.quantity,
                    order.productName,
                    discount,
                    order.unitPrice,
                    finalPrice,
                    LocalDateTime.now()
                  )
                }.toList

                logger.info(s"Batch ${index + 1} processed (${processedBatch.size})")

                // save batch
                DB.saveBatch(conn, processedBatch) match {
                  case Right(_) =>
                    conn.commit()
                    logger.info(s"Batch ${index + 1} saved")

                  case Left(err) =>
                    conn.rollback()
                    logger.severe(s"Batch ${index + 1} failed: $err")
                }
              }
          }

        } finally {
          conn.close()
          logger.info("DB connection closed")
        }

      case Left(err) =>
        logger.severe(s"DB connection failed: $err")
    }

  } catch {
    case e: Exception =>
      logger.severe(s"Pipeline error: ${e.getMessage}")
  }

  val endTime = System.nanoTime()
  val durationSeconds = (endTime - startTime) / 1e9

  logger.info("Pipeline finished")
  logger.info(f"Total time: $durationSeconds%.2f sec")
}