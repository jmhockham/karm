package com.karm.model

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue

case class ElectionSummary
(
  resourceUrl: String,
  electionType: String,
  date: String,
  label: String
) {

}

object ElectionSummary {

  implicit val formats = DefaultFormats

  def fromJson(json: JValue): ElectionSummary = {
    val resourceUrl = (json \ "_about").values.toString
    val electionType = (json \ "electionType").values.toString
    val dateStr = (json \ "date" \ "_value").values.toString
    val label = (json \ "label" \ "_value").values.toString
    new ElectionSummary(resourceUrl, electionType, dateStr, label)
  }

}