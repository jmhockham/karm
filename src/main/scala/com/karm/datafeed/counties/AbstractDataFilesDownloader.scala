package com.karm.datafeed.counties

import java.io.IOException
import java.net.{HttpURLConnection, URL}

import com.karm.dao.Database
import com.karm.model.licensing.{CompaniesHouseResult, Company}
import org.xml.sax.InputSource

import scala.xml.{Node, NodeSeq}
import scala.xml.parsing.NoBindingFactoryAdapter

trait AbstractDataFilesDownloader {

  val companyUrlPrefix = "https://beta.companieshouse.gov.uk/company/"

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
      case _: IOException =>
        //means we hit a 403 (Forbidden), because we spammed the site too much; so we sleep for 15 mins
        println("Hit IOException from search (probably a 403?), so sleeping for 15 mins")
        Thread.sleep(900000)
        conn.getInputStream
      case e:Throwable => println("ERROR: "+e.toString);null
    }
    val source = new InputSource(stream)
    adapter.loadXML(source, parser)
  }

  def searchCompaniesHouse(companyName: String): NodeSeq = {
    val sanitizedName = companyName.replaceAll(" ","%20")
    getXmlFromUrl(s"https://beta.companieshouse.gov.uk/search/companies?q=$sanitizedName")
  }

  def getCompanyIdsFromSearchResults(searchResultsHtml: NodeSeq): Seq[String] = {
    val companies = (searchResultsHtml \\ "ul").filter(node => (node \@ "id")=="results") \\ "a"
    companies.map(html => (html \@ "href").replaceAll("/company/",""))
  }

  def getCompanyResultsFromSearch(companyName: String): Seq[CompaniesHouseResult] = {
    val resultsHtml = searchCompaniesHouse(companyName)
    val companyIds = getCompanyIdsFromSearchResults(resultsHtml)
    companyIds.map(id => new CompaniesHouseResult(companyUrlPrefix + id, getCompanyData(id).mkString))
  }

  def getCompanyData(companyId: String): Node = {
    getXmlFromUrl(companyUrlPrefix + companyId)
  }

  def getPageData(pageNo: Int): NodeSeq = ???

  def persistCompaniesData(maxLimit: Int = 10): Seq[Company] = ???

  def persistAllCompanies(): Unit = {
    persistCompaniesData().map(persistCompany)
  }

  def persistCompany(company: Company): Company = {
    Database.save(company)
  }

  private def getXmlFromUrl(urlString: String): Node = {
    val url = new URL(urlString)
    xmlFromUrl(url)
  }

}
