package com.karm.model

import java.io.File

import com.karm.datafeed.DataFilesDownloader
import org.json4s.jackson.JsonMethods.parse
import org.scalatest.{FlatSpec, Matchers}

class ConstituencyTest extends FlatSpec with Matchers{

  private val constituenciesJson = new File(getClass.getResource("/constituencies.json").getPath)

  "getConstituents" should "parse the json correctly" in {
    val jValue = parse(constituenciesJson)
    val constituents = DataFilesDownloader.getConstituents(jValue)

    constituents.size shouldBe 10

    constituents.head.resourceUrl shouldBe "http://data.parliament.uk/resources/143461"
    constituents(2).resourceUrl shouldBe "http://data.parliament.uk/resources/143463"

    constituents.head.constituencyType shouldBe ""
    constituents(2).constituencyType shouldBe "County"

    constituents.head.startDate shouldBe "1918-12-14"
    constituents(2).startDate shouldBe "1974-02-28"

    constituents.head.label shouldBe "Aberavon"
    constituents(2).label shouldBe "Aberavon"
  }

  "getConstituentsJson" should "return the json data" in {
    val data = DataFilesDownloader.getConstituenciesJson
    data==null shouldBe false
    data.length>1 shouldBe true
  }

}
