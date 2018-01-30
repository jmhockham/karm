package com.karm.dao

import com.karm.datafeed.DataFilesDownloader
import com.karm.model.{Constituency, ElectionSummary, Member}
import sorm.{Entity, InitMode, Instance}

/**
  * Any entities that are going to be persisted need to be listed in the "entities" field
  */
object Database extends Instance(
  entities = Set(
    Entity[Constituency](),
    Entity[ElectionSummary](),
    Entity[Member]()
  ),
  url = "jdbc:h2:mem:test;MODE=PostgreSQL",
  user = "",
  password = "",
  timeout = 10,
  initMode = InitMode.Create
)