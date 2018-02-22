package com.karm.dao

import com.karm.model.Member
import org.scalatest.{FlatSpec, Matchers}


class MemberDaoTest extends FlatSpec with Matchers {

  DataInitialiser.init()

  "Members table" should "have data" in {
    val membersCount = Database.query[Member].count()
    membersCount>0 shouldBe true
  }

  "getLords" should "get members in the lords" in {
    val members = MembersDao.getLords()
    members.size shouldBe 2468
  }

  "getCommons" should "get members in the commons" in {
    val members = MembersDao.getCommons()
    members.size shouldBe 1966
  }
}
