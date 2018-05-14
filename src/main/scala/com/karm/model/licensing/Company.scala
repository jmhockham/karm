package com.karm.model.licensing

import scala.xml.NodeSeq

class Company (
  val companyId: Int,
  val rawData: NodeSeq
)
{

}

object Company {
  def fromCompaniesHouseResult(id: Int, rawData: NodeSeq): Company = {
    new Company(id, rawData)
  }
}