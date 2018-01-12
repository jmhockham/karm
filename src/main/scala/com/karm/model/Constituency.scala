package com.karm.model

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue

case class Constituency
(
  resourceUrl: String,
  constituencyType: String,
  startDate: String,
  label: String
) {

}

object Constituency {

  implicit val formats = DefaultFormats

  def fromJson(json: JValue): Constituency = {
    val resourceUrl = (json \ "_about").values.toString
    val constituencyType = (json \ "constituencyType").values.toString
    val dateStr = (json \ "startedDate" \ "_value").values.toString
    val label = (json \ "label" \ "_value").values.toString
    new Constituency(resourceUrl, constituencyType, dateStr, label)
  }

}