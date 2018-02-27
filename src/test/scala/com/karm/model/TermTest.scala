package com.karm.model

import java.io.File

import com.karm.datafeed.DataFilesDownloader
import org.scalatest.{FlatSpec, Matchers}

class TermTest extends FlatSpec with Matchers {

  private val memberJson = new File(getClass.getResource("/terms.json").getPath)

  "getTerms" should "return the json data" in {
    val data = DataFilesDownloader.getTermsJson
    data==null shouldBe false
    data.length>1 shouldBe true
  }

}
