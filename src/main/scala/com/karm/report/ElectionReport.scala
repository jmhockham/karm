package com.karm.report

import com.karm.dao.Database
import com.karm.datafeed.VotingDataFilesDownloader
import com.karm.model.ElectionSummary
import org.json4s.jackson.JsonMethods.parse

object ElectionReport extends App {
  private val electionsJson = VotingDataFilesDownloader.getElectionSummariesJson
  val jValue = parse(electionsJson)
  val electionSummaries = VotingDataFilesDownloader.getElectionSummariesFromJson(jValue)

  val savedStuff = electionSummaries.map { e =>
    val summary = Database.save(e)
    println("saved " + summary.toString)
    summary
  }

  println("Saved Records: " + savedStuff.size)
  private val byElectionCount = savedStuff.count(_.electionType == ElectionSummary.ELECTION_TYPE_BY)
  private val generalElectionCount = savedStuff.count(_.electionType == ElectionSummary.ELECTION_TYPE_GENERAL)
  println("Number of By-Elections: " + byElectionCount)
  println("Number of General Elections: " + generalElectionCount)
}
