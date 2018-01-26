package com.karm.model

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue

/*
This class can have a link to a constituency
 */
case class Member
(
  fullName: String,
  label: String,
  twitter: String,
  birthDate: String,
  deathDate: String,
  party: String,
  isLord: Boolean,
  constituencyUrl: String,
  constituency: Option[Constituency] = None
) {

}

object Member {

  implicit val formats = DefaultFormats

  def fromJson(json: JValue): Member = {
    val fullName = (json \ "fullName" \ "_value").values.toString
    val label = (json \ "label" \ "_value").values.toString
    val twitter = (json \ "twitter" \ "_value").values.toString
    val birthDate = (json \ "birthDate" \ "_value").values.toString
    val deathDate = (json \ "deathDate" \ "_value").values.toString
    val party = (json \ "party" \ "_value").values.toString
    val isInLords = fullName.contains("Lord") || fullName.contains("Lady")
    val constituencyUrl = (json \ "constituency" \ "_about").values.toString
    new Member(fullName, label, twitter, birthDate, deathDate, party, isInLords, constituencyUrl)
  }
}
