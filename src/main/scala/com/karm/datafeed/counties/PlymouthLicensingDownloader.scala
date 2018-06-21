package com.karm.datafeed.counties

import java.net.{HttpURLConnection, URL}

import com.karm.dao.Database
import com.karm.model.licensing.Company

import scala.xml.NodeSeq
import com.karm.utils.XmlUtils._

object PlymouthLicensingDownloader extends AbstractDataFilesDownloader {

  /*

  3-point match: gmaps api, licence data from council, company house data
  Plymouth:

  <table class="grid">
    <thead>
      <tr>
        <th class="sort_asc"><a href="/1/LicensingActPremises/Search?Column=OPR_NAME&amp;Direction=Descending&amp;page=162&amp;SearchPremisesWithRepresentationsOnly=false">Premises Name</a></th>
        <th><a href="/1/LicensingActPremises/Search?Column=OPR_PREM_ADDRESS&amp;Direction=Ascending&amp;page=162&amp;SearchPremisesWithRepresentationsOnly=false">Address</a></th>
      </tr>
    </thead>
    <tbody>
      <tr class="gridrow">
        <td><a href="/1/LicensingActPremises/Search/3028">Zizzi</a></td><td>Unit 3 Drake Circus Leisure, Bretonside, Plymouth, PL4 0BG</td></tr><tr class="gridrow_alternate"><td><a href="/1/LicensingActPremises/Search/420">Zucca Brasserie</a></td><td>North Quay, Sutton Harbour, Plymouth, Devon, PL4 0RA</td>
      </tr>
      <tr class="gridrow">
        <td><a href="/1/LicensingActPremises/Search/1741">Zuzimo Restaurant</a></td>
        <td>Ground Floor, 153 Vauxhall Street, Plymouth, Devon, PL4 0DF</td>
      </tr>
    </tbody>
  </table>

  <span class="clsIdoxPagerItemSpan clsIdoxPagerSummarySpan">Page 162 of 162 (1613 items)</span>
  https://licensing.plymouth.gov.uk/1/LicensingActPremises/Search?page=162&SearchPremisesWithRepresentationsOnly=false&Column=OPR_NAME&Direction=Ascending

  will require an api key I think:
  https://beta.companieshouse.gov.uk/search/companies?q=yukisan

  <article id="services-information-results">

        			<h1 class="visuallyhidden">Search the register – results</h1>
				<h2 class="visuallyhidden">All results</h2>
				<ul id="results" class="results-list">


                            <li class="type-company">
                                <h3>
                                    <!-- $item._score -->
                                    <a href="/company/10271532" onclick="javascript:_paq.push(['trackEvent', 'SearchSuggestions', 'SearchResult-1' ]);" title="View company">
                                        YUKI SAN  LIMITED
                                    </a>
                                </h3>

                                <p class="meta crumbtrail"> <strong>10271532</strong> - Incorporated on 11 July 2016 </p>
                                <p>C/O Yuki San, 51 Notte Street, Plymouth, England, PL1 2AG</p>
                            </li>
                </ul>


        </article>

   */

  private val baseSearchUrl = "https://licensing.plymouth.gov.uk/1/LicensingActPremises/Search"
  private val postfixUrlAgrs = "&SearchPremisesWithRepresentationsOnly=false&Column=OPR_NAME&Direction=Ascending"
  /*
  MAX_PAGE_NUMBER needs to be dynamic (can be worked out from the final span elem)
  eg:
  <span class="clsIdoxPagerItemSpan clsIdoxPagerPageLinkSpan"><a class="clsIdoxPagerOtherPageLink" href="/1/LicensingActPremises/Search?page=163&amp;SearchPremisesWithRepresentationsOnly=false&amp;Column=OPR_NAME&amp;Direction=Ascending">163</a></span>
  <span class="clsIdoxPagerItemSpan clsIdoxPagerNextPrevSpan"><a href="/1/LicensingActPremises/Search?page=2&amp;SearchPremisesWithRepresentationsOnly=false&amp;Column=OPR_NAME&amp;Direction=Ascending"><img alt="Next page of results." src="/Content/images/pagerright.jpg"></a></span>

  "Next page of results" is just after the final pageNo
  */
  val countyName = "Plymouth"

  override def getPageData(pageNo: Int): NodeSeq = {
    val urlString = baseSearchUrl + s"?page=$pageNo" + postfixUrlAgrs
    val url = new URL(urlString)
    xmlFromUrl(url)
  }

  override protected [counties] def getMaxPageResultNumber(): Int = {
    val firstPageResults = getPageData(1)
    val hrefs = (firstPageResults \\ "span" \\ "a").filter(node => (node \@ "href").contains("LicensingActPremises/Search")).map(_ \@ "href")
    val pageNoStrs = hrefs.map(href =>
      href.substring(href.indexOf("?page=")+6, href.indexOf("&"))
    )
    val sortedPageNos = pageNoStrs.map(_.toInt).sortWith(_ > _)
    sortedPageNos.head
  }

  private [counties] def getVenueUrlsFromPageHtml(html: NodeSeq): Seq[String] = {
    (html \\ "td" \ "a").filter(node => (node \@ "href").contains("LicensingActPremises/Search/") ).map(_ \@ "href")
  }

  private [counties] def getVenueNamesFromPageHtml(html: NodeSeq): Seq[String] = {
    (html \\ "td" \ "a").filter(node => (node \@ "href").contains("LicensingActPremises/Search/") ).map(_.text)
  }

  override def persistCompaniesData(maxLimit: Int = getMaxPageResultNumber()): Seq[Company] = {
    val nodeSeqs = getAllPages(maxPages = maxLimit)
    val unfilteredNames = nodeSeqs.flatMap(getVenueNamesFromPageHtml)
    val namesToProcess = if (maxLimit > 0) unfilteredNames.slice(0, maxLimit) else unfilteredNames
    namesToProcess.map { name =>
      val results = getCompanyResultsFromSearch(name)
      results.map(Database.save(_))
      if (results.size > 1) {
        val company = Company.fromMultipleSearchResults("-1", name, countyName, Nil)
        Database.save(company)
      }
      else {
        // val companyId = getCompanyIdsFromSearchResults(results.head).head
        val company = Company.fromSingleSearchResult("2", name, countyName, results.head.pageHtml)
        Database.save(company)
      }
      /*val company = Company.fromMultipleSearchResults("-1", name, countyName, Nil)
      Database.save(company)*/
    }

  // nodeSeqs.map(Company.fromSingleSearchResult("-1",countyName,_))
  }

}
