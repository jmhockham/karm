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
  constituency: Option[Constituency] = None
) {

}

object Member {

  implicit val formats = DefaultFormats

  def fromJson(json: JValue): Member = {
    val fullName = (json \ "fullName").values.toString
    val label = (json \ "label" \ "_value").values.toString
    val twitter = (json \ "twitter").values.toString
    val birthDate = (json \ "birthDate" \ "_value").values.toString
    val deathDate = (json \ "deathDate" \ "_value").values.toString
    new Member(fullName, label, twitter, birthDate, deathDate)
  }
}
