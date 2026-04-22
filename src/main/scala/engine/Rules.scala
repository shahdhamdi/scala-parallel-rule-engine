package engine

import engine.utils.safe

import java.time.temporal.ChronoUnit

object Rules {


  // =========================
  // Expire Date Rule
  // =========================

  def expireDateQualifier(days: Long): Boolean =
    days > 0 && days < 30

  def expireDateCalculation(order: Order): Double =
    safe {
      val days = ChronoUnit.DAYS.between(order.transactionDate, order.expireDate)
      if (expireDateQualifier(days)) (30 - days) / 100.0 else 0.0
    }.getOrElse(0.0)

  // =========================
  // Cheese & Wine Rule
  // =========================

  def CheeseAndWineQualifier(productName: String): Boolean =
    productName == "cheese" || productName == "wine"

  def CheeseAndWineCalculation(order: Order): Double =
    safe {
      val productName = order.productName.toLowerCase
      if (CheeseAndWineQualifier(productName)) {
        productName match {
          case "cheese" => 0.10
          case "wine"   => 0.05
          case _        => 0.0
        }
      } else 0.0
    }.getOrElse(0.0)

  // =========================
  // March 23 Rule
  // =========================

  def March23Qualifier(day: Int, month: Int): Boolean =
    day == 23 && month == 3

  def March23Calculation(order: Order): Double =
    safe {
      val day = order.transactionDate.getDayOfMonth
      val month = order.transactionDate.getMonthValue
      if (March23Qualifier(day, month))
        0.5
      else 0.0
    }.getOrElse(0.0)

  // =========================
  // Quantity Rule
  // =========================

  def ProductQuantityQualifier(quantity: Long): Boolean =
    quantity > 5

  def ProductQuantityCalculation(order: Order): Double =
    safe {
      val quantity = order.quantity
      if (ProductQuantityQualifier(quantity)) {
        quantity match {
          case q if q >= 6 && q <= 9  => 0.05
          case q if q >= 10 && q < 15 => 0.07
          case q if q >= 15           => 0.10
          case _                      => 0.0
        }
      } else 0.0
    }.getOrElse(0.0)

  // =========================
  // App Sales Rule
  // =========================

  def AppSalesQualifier(channel: String): Boolean =
    channel == "app"

  def AppSalesCalculation(order: Order): Double =
    safe {
      val channel = order.channel.toLowerCase
      if (AppSalesQualifier(channel)) {
        val discount = ((order.quantity + 4) / 5) * 5
        (discount / 5) * 0.05
      } else 0.0
    }.getOrElse(0.0)

  // =========================
  // Visa Rule
  // =========================

  def VisaQualifier(paymentMethod: String): Boolean =
    paymentMethod == "visa"

  def VisaCalculation(order: Order): Double =
    safe {
      val paymentMethod = order.payment_method.toLowerCase
      if (VisaQualifier(paymentMethod)) 0.05 else 0.0
    }.getOrElse(0.0)

  // =========================
  // Final Discount
  // =========================

  def CalculateDiscount(order: Order): Double = {
    val discounts = List(
      expireDateCalculation(order),
      CheeseAndWineCalculation(order),
      March23Calculation(order),
      ProductQuantityCalculation(order),
      AppSalesCalculation(order),
      VisaCalculation(order)
    ).filter(_ > 0)

    val top2 = discounts.sorted.reverse.take(2)

    if (top2.isEmpty) 0.0
    else top2.sum / top2.size
  }
}