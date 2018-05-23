package com.karm.model.licensing

import scala.xml.{Node, NodeSeq}

case class Company (
  val companyId: Int,
  val rawData: String
)
{

}

object Company {
  def fromCompaniesHouseResult(id: Int, rawData: NodeSeq): Company = {
    new Company(id, rawData.mkString)
  }
}