package engine
import java.time.{LocalDate, LocalDateTime}

// case class for the 2 types of data I will deal with
case class Order(
                  transactionDate: LocalDate,
                  productName: String,
                  quantity: Int,
                  expireDate: LocalDate,
                  unitPrice: Double,
                  channel: String,
                  payment_method: String
                )
case class ProcessedOrder(
                           transactionDate: LocalDate,
                           quantity: Int,
                           productName: String,
                           discount: Double,
                           unitPrice: Double,
                           finalPrice: Double,
                           processedAt: LocalDateTime
                         )