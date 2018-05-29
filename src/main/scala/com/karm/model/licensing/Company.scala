package com.karm.model.licensing

import scala.xml.{Node, NodeSeq}

case class Company (
  companyId: Int,
  countyName: String,
  rawData: String
)
{

}

object Company {
  def fromCompaniesHouseResult(id: Int, countyName: String, rawData: NodeSeq): Company = {
    new Company(id, countyName, rawData.mkString)
  }
}