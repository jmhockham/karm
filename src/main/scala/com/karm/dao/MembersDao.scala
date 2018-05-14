package com.karm.dao

import com.karm.model.voting.Member
import sorm.Dsl._

object MembersDao {
  def getLords() = {
    Database.query[Member].where("isInLords" equal true).fetch()
  }
  def getCommons() = {
    Database.query[Member].where("isInLords" equal false).fetch()
  }
}
