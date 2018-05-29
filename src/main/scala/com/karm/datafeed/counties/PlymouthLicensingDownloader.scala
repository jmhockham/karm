package com.karm.datafeed.counties

import java.net.{HttpURLConnection, URL}

import com.karm.model.licensing.Company
import org.xml.sax.InputSource

import scala.xml.{Node, NodeSeq, XML}
import com.karm.utils.XmlUtils._

import scala.annotation.tailrec
import scala.xml.parsing.NoBindingFactoryAdapter

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
  this needs to be dynamic (can be worked out from the final span elem)
  eg:
  <span class="clsIdoxPagerItemSpan clsIdoxPagerPageLinkSpan"><a class="clsIdoxPagerOtherPageLink" href="/1/LicensingActPremises/Search?page=163&amp;SearchPremisesWithRepresentationsOnly=false&amp;Column=OPR_NAME&amp;Direction=Ascending">163</a></span>
  <span class="clsIdoxPagerItemSpan clsIdoxPagerNextPrevSpan"><a href="/1/LicensingActPremises/Search?page=2&amp;SearchPremisesWithRepresentationsOnly=false&amp;Column=OPR_NAME&amp;Direction=Ascending"><img alt="Next page of results." src="/Content/images/pagerright.jpg"></a></span>

  "Next page of results" is just after the final pageNo
  */
  val MAX_PAGE_NUMBER = 162

  override def getPageData(pageNo: Int): NodeSeq = {
    val urlString = baseSearchUrl + s"?page=$pageNo" + postfixUrlAgrs
    val url = new URL(urlString)
    xmlFromUrl(url)
  }

  @tailrec
  def getAllPages(currentPageNo: Int = 1, seqToReturn: Seq[NodeSeq] = Seq.empty): Seq[NodeSeq] = {
    val pageData = getPageData(currentPageNo)
    if (currentPageNo == MAX_PAGE_NUMBER) {
      seqToReturn ++ pageData
    }
    else {
      getAllPages(currentPageNo + 1, seqToReturn ++ pageData)
    }
  }

  val countyName = "Plymouth"

  override def getCompaniesData(): Seq[Company] = {
    val nodeSeqs = getAllPages()
    nodeSeqs.map(Company.fromCompaniesHouseResult(-1,countyName,_))
  }

}
