package com.karm.model.licensing

import com.karm.utils.UrlFunctions

import scala.xml.{Node, NodeSeq}

case class Company (
   companyId: String,
   name: String,
   countyName: String,
   companiesHouseResults: Seq[CompaniesHouseResult],
   rawData: String
) /*extends UrlFunctions*/
{
  /**
    * Goes off to companies house and gets the search results (from trying to find this companies name)
    * @return the xml of the search results page (limited to the first 20 entries)
    */
  /*def companiesHouseSearchResults: NodeSeq = {
    searchCompaniesHouse(name)
  }*/
}

object Company {
  def fromSingleSearchResult(id: String, name: String, countyName: String, rawData: String): Company = {
    new Company(
      companyId = id,
      name = name,
      countyName = countyName,
      companiesHouseResults = Nil,
      rawData = rawData
    )
  }

  def fromMultipleSearchResults(id: String, name: String, countyName: String, searchResults: Seq[CompaniesHouseResult]): Company = {
    new Company(
      companyId = id,
      name = name,
      countyName = countyName,
      companiesHouseResults = searchResults,
      rawData = ""
    )
  }
}