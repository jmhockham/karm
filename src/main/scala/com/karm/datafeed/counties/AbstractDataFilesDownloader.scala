package com.karm.datafeed.counties

import java.io.IOException
import java.net.{HttpURLConnection, URL}

import com.karm.dao.Database
import com.karm.model.licensing.{CompaniesHouseResult, Company}
import org.xml.sax.InputSource

import scala.xml.{Node, NodeSeq}
import scala.xml.parsing.NoBindingFactoryAdapter

trait AbstractDataFilesDownloader {

  val COMPANIES_HOUSE_URL_PREFIX = "https://beta.companieshouse.gov.uk/company/"
  val COMPANIES_HOUSE_API_KEY = "T8a-MEUBsY6wXn4SedbuWOiW6fD2ZxE1ivL3uBmM"
  val GOOGLE_API_KEY = "AIzaSyDKudzx8i1Z4WJLcbydUGJjF1SxT1Ayxo0"

  protected def callUrl(url: String): String = {
    scala.io.Source.fromURL(url).mkString
  }

  import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl

  lazy val adapter = new NoBindingFactoryAdapter
  lazy val parser = (new SAXFactoryImpl).newSAXParser

  def xmlFromUrl(url: URL, headers: Map[String, String] = Map.empty): Node = {
    val conn = url.openConnection().asInstanceOf[HttpURLConnection]
    for ((k, v) <- headers)
      conn.setRequestProperty(k, v)
    val stream = try {
      conn.getInputStream
    } catch {
      case ioe: IOException =>
        //means we hit a 403 (Forbidden), because we spammed the site too much; so we sleep for 15 mins
        println("Hit IOException from search (probably a 403?), so sleeping for 30 mins")
        println("Exception msg is: "+ioe.getMessage)
        conn.disconnect()
        Thread.sleep(1800000)
        conn.connect()
        conn.getInputStream
      case e:Throwable => println("ERROR: "+e.toString);null
    }
    val source = new InputSource(stream)
    val node = adapter.loadXML(source, parser)
    conn.disconnect()
    node
  }

  def searchCompaniesHouse(companyName: String): NodeSeq = {
    val sanitizedName = companyName.replaceAll(" ","%20")
    val headers = Map("Authorization" -> s"Basic $COMPANIES_HOUSE_API_KEY")
    getXmlFromUrl(s"https://beta.companieshouse.gov.uk/search/companies?q=$sanitizedName", headers)
  }

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

  private def getXmlFromUrl(urlString: String, headers: Map[String, String] = Map.empty): Node = {
    val url = new URL(urlString)
    xmlFromUrl(url, headers)
  }

}
