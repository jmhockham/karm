package com.karm.datafeed.counties

import org.scalatest.{FlatSpec, Matchers}

class LondonLicensingDownloaderTest extends FlatSpec with Matchers {

  "getPageData" should "return an html result" in {
    val html = LondonLicensingDownloader.getPageData(2)
    html.nonEmpty shouldBe true
    val venueUrls = (html \\ "td" \ "a").filter(node => (node \@ "href").contains("LicenceRegistersDetails.xsl") ).map(_ \@ "href")
    venueUrls.nonEmpty shouldBe true
  }

  "getVenueUrlsFromPageHtml" should "return a collection of company urls" in {
    val html = LondonLicensingDownloader.getPageData(2)
    val venueUrls = LondonLicensingDownloader.getVenueUrlsFromPageHtml(html)
    venueUrls.size>1 shouldBe true

    //we get the last page if the pageNo is out of bounds
    val htmlNoResult = LondonLicensingDownloader.getPageData(10000)
    val venueUrlsNoResult = LondonLicensingDownloader.getVenueUrlsFromPageHtml(htmlNoResult)
    venueUrlsNoResult.size<3 shouldBe true
  }

  "getVenueNamesFromPageHtml" should "return a collection of company urls" in {
    val html = LondonLicensingDownloader.getPageData(2)
    val venueNames = LondonLicensingDownloader.getVenueNamesFromPageHtml(html)
    venueNames.nonEmpty shouldBe true

    //we get the last page if the pageNo is out of bounds
    val htmlNoResult = LondonLicensingDownloader.getPageData(10000)
    val venueNamesNoResult = LondonLicensingDownloader.getVenueUrlsFromPageHtml(htmlNoResult)
    venueNamesNoResult.nonEmpty shouldBe true
  }
  
}
