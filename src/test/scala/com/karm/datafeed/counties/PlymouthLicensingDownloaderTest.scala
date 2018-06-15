package com.karm.datafeed.counties

import com.karm.dao.Database
import com.karm.model.licensing.Company
import org.scalatest.{FlatSpec, Matchers}
import com.karm.utils.XmlUtils._

class PlymouthLicensingDownloaderTest extends FlatSpec with Matchers {
  "getPageData" should "return the html for a url call" in {
    val html = PlymouthLicensingDownloader.getPageData(2)
    html.nonEmpty shouldBe true
    val venueUrls = (html \\ "td" \ "a").filter(node => (node \@ "href").contains("LicensingActPremises/Search/") ).map(_ \@ "href")
    venueUrls.nonEmpty shouldBe true
  }

  "searchCompaniesHouse" should "query companies house" in {
    val html = PlymouthLicensingDownloader.searchCompaniesHouse("yukisan")
    html.nonEmpty shouldBe true
    val companies = (html \\ "ul").filter(node => (node \@ "id")=="results") \\ "a"
    companies.text.replaceAll("\n","").trim.toUpperCase shouldBe "YUKI SAN LIMITED"
  }

  "the case class" should "parse the raw data, and be persisted" in {
    val companyId = "10271532"
    val rawHtml = PlymouthLicensingDownloader.getCompanyData(companyId)
    val company: Company = Company.fromSingleSearchResult(companyId, "test", "Plymouth", rawHtml.mkString)
    val companyFromPersist = Database.save(company)
    companyFromPersist.companyId shouldBe companyId
  }

  "all page data" should "be able to be persisted" in {
    val pageHtml = PlymouthLicensingDownloader.getAllPages()
    val companies = pageHtml.map(html => Company.fromSingleSearchResult("-1", "test", "Plymouth", html.mkString))
    val savedResults = companies.map(Database.save(_))
    savedResults.size shouldBe 164
  }

  "parseCompaniesHouseSearchResults" should "handle multiple results" in {
    val html = PlymouthLicensingDownloader.searchCompaniesHouse("zizi")
    html.nonEmpty shouldBe true
    val companyIds = PlymouthLicensingDownloader.getCompanyIdsFromSearchResults(html)
    companyIds.size>1 shouldBe true
  }

  "getCompanyResultsFromSearch" should "create models to hold the search results" in {
    val ziziResults = PlymouthLicensingDownloader.getCompanyResultsFromSearch("zizi")
    val yukisanResults = PlymouthLicensingDownloader.getCompanyResultsFromSearch("yukisan")
    ziziResults.size shouldBe 20
    yukisanResults.size shouldBe 1
  }

  "getVenueUrlsFromPageHtml" should "return a company url, or nothing for an incorrect pageNo" in {
    val html = PlymouthLicensingDownloader.getPageData(2)
    val venueUrls = PlymouthLicensingDownloader.getVenueUrlsFromPageHtml(html)
    venueUrls.size>1 shouldBe true

    val htmlNoResult = PlymouthLicensingDownloader.getPageData(1000)
    val venueUrlsNoResult = PlymouthLicensingDownloader.getVenueUrlsFromPageHtml(htmlNoResult)
    venueUrlsNoResult.size<1 shouldBe true
  }

  "getVenueNamesFromPageHtml" should "return a company url, or nothing for an incorrect pageNo" in {
    val html = PlymouthLicensingDownloader.getPageData(2)
    val venueNames = PlymouthLicensingDownloader.getVenueNamesFromPageHtml(html)
    venueNames.size>1 shouldBe true

    val htmlNoResult = PlymouthLicensingDownloader.getPageData(1000)
    val venueNamesNoResult = PlymouthLicensingDownloader.getVenueUrlsFromPageHtml(htmlNoResult)
    venueNamesNoResult.size<1 shouldBe true
  }

  "getCompaniesData" should "parse the html results into company entities" in {
    val companies = PlymouthLicensingDownloader.persistCompaniesData(1)
    companies.nonEmpty shouldBe true
    companies.size shouldBe 1
  }

  "getMaxPageResultNumber" should "return the max number of search pages" in {
    val max = PlymouthLicensingDownloader.getMaxPageResultNumber()
    max shouldBe 164
  }
}
