package com.karm.dao

import com.karm.datafeed.VotingDataFilesDownloader
import com.karm.model.licensing.Company
import com.karm.model.voting.{Constituency, ElectionSummary, Member}
import sorm.{Entity, InitMode, Instance}

/**
  * Any entities that are going to be persisted need to be listed in the "entities" field
  */
object Database extends Instance(
  entities = Set(
    Entity[Constituency](),
    Entity[ElectionSummary](),
    Entity[Member](),
    Entity[Company]()
  ),
//  url = "jdbc:h2:mem:test;MODE=PostgreSQL",
  url = "jdbc:postgresql://localhost/karm",
  user = "karm",
  password = "karm",
  timeout = 10,
  initMode = InitMode.DropAllCreate
)
