package com.karm.model.licensing

import scala.xml.{Node, NodeSeq}

case class Company (
   companyId: String,
   countyName: String,
   companiesHouseResults: Seq[CompaniesHouseResult],
   rawData: String
)
{

}

object Company {
  def fromSingleSearchResult(id: String, countyName: String, rawData: NodeSeq): Company = {
    new Company(id, countyName, Nil, rawData.mkString)
  }

  def fromMultipleSearchResults(id: String, countyName: String, searchResults: Seq[CompaniesHouseResult]): Company = {
    new Company(id, countyName, searchResults, "")
  }
}