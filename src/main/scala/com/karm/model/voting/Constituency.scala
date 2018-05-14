package com.karm.model.voting

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue

case class Constituency
(
  resourceUrl: String,
  constituencyType: String,
  startDate: String,
  endDate: String,
  gssCode: String,
  osName: String,
  label: String
) {

}

object Constituency {

  implicit val formats = DefaultFormats

  def fromJson(json: JValue): Constituency = {
    val resourceUrl = (json \ "_about").values.toString
    val constituencyType = (json \ "constituencyType").values.toString
    val startDate = (json \ "startedDate" \ "_value").values.toString
    val endDate = (json \ "endededDate" \ "_value").values.toString
    val gssCode = (json \ "gssCode").values.toString
    val osName = (json \ "osName").values.toString
    val label = (json \ "label" \ "_value").values.toString

    new Constituency(
      resourceUrl = resourceUrl,
      constituencyType = constituencyType,
      startDate = startDate,
      endDate = endDate,
      gssCode = gssCode,
      osName = osName,
      label = label
    )
  }

}