package com.karm.model

import com.fasterxml.jackson.annotation.JsonValue
import org.json4s.{DefaultFormats, JValue}

case class Term
(
  about: String,
  attribute: String,
  classification: String,
  broaderTerm: Option[Term],
  exactTerm: Option[Term],
  isPreferred: Option[Boolean],
  prefLabel: String
) {

}

object Term {

  implicit val formats = DefaultFormats

  def fromJson(json: JValue): Term ={
    new Term(
      about = (json \ "_about").values.toString,
      attribute = (json \ "attribute").values.toString,
      classification = (json \ "class").values.toString,
      broaderTerm = None,
      exactTerm = None,
      isPreferred = None,
      prefLabel = (json \ "prefLabel" \ "_value").values.toString
    )
  }

}
