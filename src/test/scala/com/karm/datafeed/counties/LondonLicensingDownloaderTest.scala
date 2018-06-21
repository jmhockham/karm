package com.karm.datafeed.counties

import org.scalatest.{FlatSpec, Matchers}

class LondonLicensingDownloaderTest extends FlatSpec with Matchers {

  "getPageData" should "return an html result" in {
    val html = LondonLicensingDownloader.getPageData(2)
    html.nonEmpty shouldBe true
    val venueUrls = (html \\ "td" \ "a").filter(node => (node \@ "href").contains("LicenceRegistersDetails.xsl") ).map(_ \@ "href")
    venueUrls.nonEmpty shouldBe true
  }

}
