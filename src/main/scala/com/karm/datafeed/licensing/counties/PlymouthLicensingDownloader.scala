package com.karm.datafeed.counties

import java.net.URL

import com.karm.dao.Database
import com.karm.model.licensing.Company

import scala.xml.NodeSeq

object PlymouthLicensingDownloader extends AbstractDataFilesDownloader {

  private val baseSearchUrl = "https://licensing.plymouth.gov.uk/1/LicensingActPremises/Search"
  private val postfixUrlAgrs = "&SearchPremisesWithRepresentationsOnly=false&Column=OPR_NAME&Direction=Ascending"
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
