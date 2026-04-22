package engine

import java.sql.{Connection, DriverManager}
import com.typesafe.config.ConfigFactory

object DB_connection {
// load config file to connect to db
   val config = ConfigFactory.load()

   val url = config.getString("db.url")
   val user = config.getString("db.user")
   val password = config.getString("db.password")

  def getConnection(): Connection = {
     Class.forName("com.mysql.cj.jdbc.Driver")
     DriverManager.getConnection(url, user, password)
  }
}