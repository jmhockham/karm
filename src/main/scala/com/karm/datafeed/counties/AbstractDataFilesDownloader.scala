package com.karm.datafeed.counties

import java.io.IOException
import java.net.{HttpURLConnection, URL}

import com.karm.dao.Database
import com.karm.model.licensing.{CompaniesHouseResult, Company}
import com.karm.utils.UrlFunctions
import org.xml.sax.InputSource

import scala.annotation.tailrec
import scala.xml.{Node, NodeSeq}
import scala.xml.parsing.NoBindingFactoryAdapter

trait AbstractDataFilesDownloader extends UrlFunctions {

  def getCompanyIdsFromSearchResults(searchResultsHtml: NodeSeq): Seq[String] = {
    val companies = (searchResultsHtml \\ "ul").filter(node => (node \@ "id")=="results") \\ "a"
    companies.map(html => (html \@ "href").replaceAll("/company/",""))
  }

  def getCompanyResultsFromSearch(companyName: String): Seq[CompaniesHouseResult] = {
    val resultsHtml = searchCompaniesHouse(companyName)
    val companyIds = getCompanyIdsFromSearchResults(resultsHtml)
    companyIds.map(id => new CompaniesHouseResult(COMPANIES_HOUSE_URL_PREFIX + id, getCompanyData(id).mkString))
  }

  def getCompanyData(companyId: String): Node = {
    getXmlFromUrl(COMPANIES_HOUSE_URL_PREFIX + companyId)
  }

  protected [counties] def getMaxPageResultNumber(): Int = ???

  @tailrec
  final def getAllPages(currentPageNo: Int = 1, seqToReturn: Seq[NodeSeq] = Seq.empty, maxPages: Int = getMaxPageResultNumber()): Seq[NodeSeq] = {
    val pageData = getPageData(currentPageNo)
    if (currentPageNo == maxPages) {
      seqToReturn ++ pageData
    }
    else {
      getAllPages(currentPageNo + 1, seqToReturn ++ pageData)
    }
  }

  def getPageData(pageNo: Int): NodeSeq = ???

  def persistCompaniesData(maxLimit: Int = 10): Seq[Company] = ???

  def persistAllCompanies(): Unit = {
    persistCompaniesData().map(persistCompany)
  }

  def persistCompany(company: Company): Company = {
    Database.save(company)
  }

  //Google API key:
  val googleApiKey = "AIzaSyDKudzx8i1Z4WJLcbydUGJjF1SxT1Ayxo0"

  def getMapsSearchUrl(queryTerms: String): String = {
    val sanitizedSearchTerms = queryTerms.replaceAll(" ","+").replaceAll(",","%2C")
    val url = s"https://www.google.com/maps/search/?api=$googleApiKey&query=$sanitizedSearchTerms"
    url
  }

  def gtMapsGeocodingUrl(address: String): String = {
    val sanitizedAddress = address.replaceAll(" ","+").replaceAll(",","%2C")
    val url = s"https://maps.googleapis.com/maps/api/geocode/json?address=$sanitizedAddress&key=$googleApiKey"
    url
  }

}
