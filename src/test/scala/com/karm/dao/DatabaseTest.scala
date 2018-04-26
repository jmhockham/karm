package com.karm.dao

import java.io.File

import com.karm.datafeed.VotingDataFilesDownloader
import com.karm.model.{Constituency, ElectionSummary, Member}
import org.json4s.jackson.JsonMethods.parse
import org.scalatest.{FlatSpec, Matchers}

class DatabaseTest extends FlatSpec with Matchers {

  private val electionJson = new File(getClass.getResource("/election-summaries.json").getPath)
  private val constituenciesJson = new File(getClass.getResource("/constituencies.json").getPath)
  private val membersJson = new File(getClass.getResource("/members.json").getPath)

  "Database" should "be able to persist constituency entities" in {
    val jValue = parse(electionJson)
    val constituencies = VotingDataFilesDownloader.getConstituentsFromJson(jValue)

    //we can assign the results of an insert to a value
    val constituency = Database.save(constituencies.head)
    constituency.id shouldBe 1

    val maybeConstituency = Database.query[Constituency].whereEqual("id", 1).fetchOne()
    maybeConstituency.nonEmpty shouldBe true
    maybeConstituency.get.id shouldBe 1
  }

  it should "be able to persist election entities" in {
    val jValue = parse(constituenciesJson)
    val electionSummaries = VotingDataFilesDownloader.getElectionSummariesFromJson(jValue)

    val electionSummary = Database.save(electionSummaries.head)
    electionSummary.id shouldBe 1

    val maybeElectionSummary = Database.query[ElectionSummary].whereEqual("id", 1).fetchOne()
    maybeElectionSummary.nonEmpty shouldBe true
    maybeElectionSummary.get.id shouldBe 1
  }

  it should "be able to persist member entities" in {
    val jValue = parse(membersJson)
    val members = VotingDataFilesDownloader.getMembersFromJson(jValue)

    val member = Database.save(members.head)
    member.id shouldBe 1

    val maybeMember = Database.query[Member].whereEqual("id", 1).fetchOne()
    maybeMember.nonEmpty shouldBe true
    maybeMember.get.id shouldBe 1
  }

}
