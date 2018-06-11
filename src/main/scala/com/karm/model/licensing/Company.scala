package com.karm.model.licensing

import scala.xml.{Node, NodeSeq}

case class Company (
   companyId: String,
   name: String,
   countyName: String,
   companiesHouseResults: Seq[CompaniesHouseResult],
   rawData: String
)
{

}

object Company {
  def fromSingleSearchResult(id: String, name: String, countyName: String, rawData: String): Company = {
    new Company(id, name=name, countyName, Nil, rawData)
  }

  def fromMultipleSearchResults(id: String, name: String, countyName: String, searchResults: Seq[CompaniesHouseResult]): Company = {
    new Company(id, name=name, countyName, searchResults, "")
  }
}