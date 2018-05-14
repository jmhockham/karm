package com.karm.model

import java.io.File

import com.karm.datafeed.VotingDataFilesDownloader
import com.karm.model.voting.Member
import org.json4s.jackson.JsonMethods.parse
import org.scalatest.{FlatSpec, Matchers}

class MemberTest extends FlatSpec with Matchers {

  private val memberJson = new File(getClass.getResource("/members.json").getPath)

  "getMembers" should "parse the json correctly" in {
    val jValue = parse(memberJson)
    val members = VotingDataFilesDownloader.getMembersFromJson(jValue)

    members.size shouldBe 4434

    val head: Member = members.head
    head.fullName shouldBe "Ms Diane Abbott"
    head.label shouldBe "Biography information for Ms Diane Abbott"
    head.twitter shouldBe "https://twitter.com/HackneyAbbott"
    head.party shouldBe "Labour"
    head.constituency shouldBe None
  }

  "getMembersJson" should "return the json data" in {
    val data = VotingDataFilesDownloader.getMembersJson
    data==null shouldBe false
    data.length>1 shouldBe true
  }

}
