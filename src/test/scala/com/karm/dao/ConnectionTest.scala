package com.karm.dao

import java.sql.{Connection, DriverManager}
import java.util.Properties

import org.scalatest.{FlatSpec, Matchers}

class ConnectionTest extends FlatSpec with Matchers {
  "Postgres database" should "exist, and be able to establish a connection" in {
    val props: Properties = new Properties()
    props.setProperty("user","karm")
    props.setProperty("password","karm")
    val conn = DriverManager.getConnection("jdbc:postgresql://localhost/karm",props)
    conn.close()
  }
}
