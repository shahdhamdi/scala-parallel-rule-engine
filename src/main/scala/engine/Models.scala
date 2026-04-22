package engine
import java.time.{LocalDate, LocalDateTime}

// Data models used across the pipeline: raw input (Order) and computed output (ProcessedOrder)
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