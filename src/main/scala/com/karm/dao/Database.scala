package com.karm.dao

import java.util.Locale

import sorm.{Entity, InitMode, Instance}

class Database {
  val dbUrl = "jdbc:postgresql://localhost/test"
}

object Database extends Instance(


  entities = Set(
//    Entity[Constituency](),
//    Entity[ElectionSummary](),
    Entity[Locale](unique = Set() + Seq("code"))
  ),
  url = "jdbc:postgresql://localhost/test",
  user = "postgres",
  password = "password",
  initMode = InitMode.Create
)
