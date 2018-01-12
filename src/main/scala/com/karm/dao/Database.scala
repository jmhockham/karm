package com.karm.dao

import com.karm.model.{Constituency, ElectionSummary}
import sorm.{Entity, InitMode, Instance}

object Database extends Instance(
  entities = Set(
    Entity[Constituency](),
    Entity[ElectionSummary]()
  ),
  url = "jdbc:h2:mem:test;MODE=PostgreSQL",
  user = "",
  password = "",
  timeout = 10,
  initMode = InitMode.Create
)
