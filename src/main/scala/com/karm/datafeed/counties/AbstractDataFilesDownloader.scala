package com.karm.datafeed.counties

import java.net.{HttpURLConnection, URL}

import com.karm.dao.Database
import com.karm.model.licensing.Company
import org.xml.sax.InputSource

import scala.xml.{Node, NodeSeq}
import scala.xml.parsing.NoBindingFactoryAdapter

trait AbstractDataFilesDownloader {

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
    val source = new InputSource(conn.getInputStream)
    adapter.loadXML(source, parser)
  }

  def searchCompaniesHouse(companyName: String): Node = {
    getXmlFromUrl(s"https://beta.companieshouse.gov.uk/search/companies?q=$companyName")
  }

  def getCompanyData(companyId: Int): Node = {
    getXmlFromUrl(s"https://beta.companieshouse.gov.uk/company/$companyId")
  }

  def getPageData(pageNo: Int): NodeSeq = ???

  def persistCompany(company: Company): Company = {
    Database.save(company)
  }

  private def getXmlFromUrl(urlString: String): Node = {
    val url = new URL(urlString)
    xmlFromUrl(url)
  }

}
