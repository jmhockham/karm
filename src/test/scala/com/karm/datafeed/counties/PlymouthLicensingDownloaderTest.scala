package com.karm.datafeed.counties

import com.karm.dao.Database
import com.karm.model.licensing.Company
import org.scalatest.{FlatSpec, Matchers}
import com.karm.utils.XmlUtils._

class PlymouthLicensingDownloaderTest extends FlatSpec with Matchers {
  "getPageData" should "return the html for a url call" in {
    val html = PlymouthLicensingDownloader.getPageData(162)
    html.nonEmpty shouldBe true
    val venueUrls = (html \\ "td" \ "a").filter(node => (node \@ "href").contains("LicensingActPremises/Search/") ).map(_ \@ "href")
    venueUrls.nonEmpty shouldBe true
  }

  "searchCompaniesHouse" should "query companies house" in {
    val html = PlymouthLicensingDownloader.searchCompaniesHouse("yukisan")
    html.nonEmpty shouldBe true
    val companies = (html \\ "ul").filter(node => (node \@ "id")=="results") \\ "a"
    /*
    <a title="View company" onclick="javascript:_paq.push(['trackEvent', 'SearchSuggestions', 'SearchResult-1' ]);" href="/company/10271532" shape="rect">
                                        YUKI SAN LIMITED
                                    </a>
    */


    companies.text.replaceAll("\n","").trim.toUpperCase shouldBe "YUKI SAN LIMITED"
  }

  "the case class" should "parse the raw data, and be persisted" in {
    val companyId = 10271532
    val rawHtml = PlymouthLicensingDownloader.getCompanyData(companyId)
    val company = Company.fromCompaniesHouseResult(companyId, rawHtml)
    val companyFromPersist = Database.save(company)
    companyFromPersist.companyId shouldBe companyId
  }
}
