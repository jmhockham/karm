package com.karm.datafeed

import com.karm.model.{Constituency, ElectionSummary, Member}
import org.json4s.{DefaultFormats, JValue}
import org.json4s.jackson.JsonMethods.parse

object DataFilesDownloader extends App {

  implicit val formats = DefaultFormats

  /* assigned the web calls to values so that I can easily breakpoint + debug */

  /*
  http://lda.data.parliament.uk/members.json
  http://lda.data.parliament.uk/members.json?_pageSize=5000&_page=0


   */

  def getElectionSummariesJson: String = {
    val json = callUrl("http://lda.data.parliament.uk/elections.json?_view=Elections&_pageSize=1000&_page=0")
    json
  }

  def getConstituenciesJson: String = {
    val json = callUrl("http://lda.data.parliament.uk/constituencies.json?_pageSize=5000&_page=0")
    json
  }

  def getMembersJson: String = {
    val json = callUrl("http://lda.data.parliament.uk/members.json?_pageSize=5000&_page=0")
    json
  }

  def getElectionSummariesFromJson(json: JValue): List[ElectionSummary] = {
    (json \\ "items").children.map { x =>
      ElectionSummary.fromJson(x)
    }
  }

  def getConstituentsFromJson(json: JValue): List[Constituency] = {
    (json \\ "items").children.map { x =>
      Constituency.fromJson(x)
    }
  }

  def getMembersFromJson(json: JValue): List[Member] = {
    (json \\ "items").children.map { x =>
      Member.fromJson(x)
    }
  }

  def getElectionSummaries(): List[ElectionSummary] = {
    val jValue = parse(getElectionSummariesJson)
    getElectionSummariesFromJson(jValue)
  }

  def getConstituents(): List[Constituency] = {
    val jValue = parse(getConstituenciesJson)
    getConstituentsFromJson(jValue)
  }

  def getMembers(): List[Member] = {
    val jValue = parse(getMembersJson)
    getMembersFromJson(jValue)
  }

  private def callUrl(url: String): String = {
    scala.io.Source.fromURL(url).mkString
  }

}
