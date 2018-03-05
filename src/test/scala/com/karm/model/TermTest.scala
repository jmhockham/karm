package com.karm.model

import java.io.File

import com.karm.datafeed.DataFilesDownloader
import com.karm.datafeed.DataFilesDownloader._
import org.scalatest.{FlatSpec, Matchers}

class TermTest extends FlatSpec with Matchers {

  private val memberJson = new File(getClass.getResource("/terms.json").getPath)

  "getTerms" should "return the json data" in {
    val data = DataFilesDownloader.getTermsJson(TERMS_PAGE_LIMIT)
    data==null shouldBe false
    data.length shouldBe 5000
  }

}
