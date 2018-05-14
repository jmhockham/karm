package com.karm.model.voting

import org.json4s.{DefaultFormats, JValue}

case class Term
(
  about: String,
  attribute: String,
  classification: String,
  broaderTermJson: Option[JValue],
  broaderTerm: Option[Term],
  exactTermJson: Option[JValue],
  exactTerm: Option[Term],
  isPreferred: Option[Boolean],
  prefLabel: String
) {

}

object Term {

  implicit val formats = DefaultFormats

  def fromJson(json: JValue): Term ={
    val prefString = (json \ "isPreferred" \ "_value").values.toString
    new Term(
      about = (json \ "_about").values.toString,
      attribute = (json \ "attribute").values.toString,
      classification = (json \ "class").values.toString,
      broaderTermJson = (json \ "broader").toOption,
      broaderTerm = None,
      exactTermJson = (json \ "exactMatch").toOption,
      exactTerm = None,
      isPreferred = if (prefString.nonEmpty) {
        Some(prefString == "true")
      } else {
        None
      },
      prefLabel = (json \ "prefLabel" \ "_value").values.toString
    )
  }

}
