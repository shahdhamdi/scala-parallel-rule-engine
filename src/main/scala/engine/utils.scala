package engine

import java.time.LocalDate
import scala.io.{Codec, Source}
import scala.util.Using

object utils {

  def safe[A](block: => A): Option[A] =
    try {
      Some(block)
    } catch {
      case _: Exception => None
    }
  def parseLine(line: String): Order = {
    val parts = line.split(",")

    Order(
      transactionDate = LocalDate.parse(parts(0).substring(0, 10)),
      productName = parts(1),
      expireDate = LocalDate.parse(parts(2)),
      quantity = parts(3).toInt,
      unitPrice = parts(4).toDouble,
      channel = parts(5),
      payment_method = parts(6)
    )
  }
}
