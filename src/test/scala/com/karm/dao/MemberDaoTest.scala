package com.karm.dao

import org.scalatest.{FlatSpec, Matchers}

class MemberDaoTest extends FlatSpec with Matchers{
  "getLords" should "get members in the lords" in {
    DataInitialiser.init()
    val members = MembersDao.getLords()
    members.size shouldBe 1
  }
}
