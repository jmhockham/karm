package com.karm.model

import java.io.File

import com.karm.datafeed.DataFilesDownloader
import org.json4s.jackson.JsonMethods.parse
import org.scalatest.{FlatSpec, Matchers}

class TermTest extends FlatSpec with Matchers {

  private val termsJson = new File(getClass.getResource("/terms.json").getPath)

  "getTermsFromJson" should "parse the json data correctly" in {
    val jValue = parse(termsJson)
    val terms = DataFilesDownloader.getTermsFromJson(jValue)

    terms.size shouldBe 10000

    val head: Term = terms.head
    head.about shouldBe "http://data.parliament.uk/terms/97792"
    head.attribute shouldBe "ORG"
    head.classification shouldBe ""
    head.broaderTerm shouldBe None
    head.exactTerm shouldBe None
    head.isPreferred shouldBe None
    head.prefLabel shouldBe "'Sdim Curo Plant!"
  }

  "getTermsJson" should "return the json data" in {
    val data = DataFilesDownloader.getTermsJson(0)
    data==null shouldBe false
    data.length>0 shouldBe true
  }

  "getTerms" should "get all the terms" in {
    val terms = DataFilesDownloader.getTerms(7)
    terms.size shouldBe 79576
  }

}
