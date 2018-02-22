package com.karm.model

case class Term
(
  about: String,
  attribute: String,
  classification: String,
  broaderTerm: Option[Term],
  exactTerm: Option[Term],
  isPreferred: Boolean,
  prefLabel: String
) {

}
