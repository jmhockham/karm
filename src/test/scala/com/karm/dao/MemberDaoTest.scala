package com.karm.dao

import com.karm.model.Member
import org.scalatest.{FlatSpec, Matchers}


class MemberDaoTest extends FlatSpec with Matchers {

  VotingDataInitialiser.init()

  //we're going to use rough counts, as the live data can change as members come and go
  "Members table" should "have data" in {
    val membersCount = Database.query[Member].count()
    membersCount>4000 shouldBe true
  }

  "getLords" should "get members in the lords" in {
    val members = MembersDao.getLords()
    members.size>2000 shouldBe true
  }

  "getCommons" should "get members in the commons" in {
    val members = MembersDao.getCommons()
    members.size>1500 shouldBe true
  }

  "Members" should "have a constituency" in {
    val member = Database.query[Member].fetchOne().head
    val constituencyOption = member.constituency
    constituencyOption.nonEmpty shouldBe true
  }
}
