package engine

import java.sql.Connection

object DB {

  def saveBatch(conn: Connection, orders: List[ProcessedOrder]): Either[String, Unit] = {
    try {
      val sql =
        """
          INSERT INTO processed_orders
          (transaction_date, quantity, product_name, discount, unit_price, final_price, processed_at)
          VALUES (?, ?, ?, ?, ?, ?, ?)
        """

      val stmt = conn.prepareStatement(sql)

      orders.foreach { po =>
        stmt.setDate(1, java.sql.Date.valueOf(po.transactionDate))
        stmt.setInt(2, po.quantity)
        stmt.setString(3, po.productName)
        stmt.setDouble(4, po.discount)
        stmt.setDouble(5, po.unitPrice)
        stmt.setDouble(6, po.finalPrice)
        stmt.setTimestamp(7, java.sql.Timestamp.valueOf(po.processedAt))
        stmt.addBatch()
      }

      stmt.executeBatch()
      stmt.close()

      Right(())

    } catch {
      case e: Exception =>
        Left(e.getMessage)
    }
  }
}