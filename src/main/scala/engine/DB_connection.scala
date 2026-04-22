package engine

import java.sql.{Connection, DriverManager}
import com.typesafe.config.ConfigFactory
import scala.util.Try

object DB_connection {

  // Load configuration once
  private val config = ConfigFactory.load()

  private val url      = config.getString("db.url")
  private val user     = config.getString("db.user")
  private val password = config.getString("db.password")

  // Safe connection creation
  def getConnection(): Either[String, Connection] = {
    Try {
      Class.forName("com.mysql.cj.jdbc.Driver")
      DriverManager.getConnection(url, user, password)
    }.toEither.left.map(_.getMessage)
  }
}