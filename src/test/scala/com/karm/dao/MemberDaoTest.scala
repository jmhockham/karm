package com.karm.dao

import org.scalatest.{FlatSpec, Matchers}

class MemberDaoTest extends FlatSpec with Matchers{

  DataInitialiser.init()

  "getLords" should "get members in the lords" in {
    val members = MembersDao.getLords()
    members.size shouldBe 2468
  }

  "getCommons" should "get members in the commons" in {
    val members = MembersDao.getCommons()
    members.size shouldBe 1966
  }
}
