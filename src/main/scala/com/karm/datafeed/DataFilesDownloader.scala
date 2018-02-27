package com.karm.datafeed

import com.karm.model.{Constituency, ElectionSummary, Member, Term}
import org.json4s.{DefaultFormats, JValue}
import org.json4s.jackson.JsonMethods.parse

object DataFilesDownloader extends App {

  implicit val formats = DefaultFormats

  /* assigned the web calls to values so that I can easily breakpoint + debug */

  /*
  format for a given entity, and the page size parameter
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

  //there are a bit less than 115,000 terms
  //TODO append the rest of the terms - probably don't want to get them all in one hit
  def getTermsJson: String = {
    val json = callUrl("http://lda.data.parliament.uk/terms.json?_pageSize=5000&_page=0")
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

  def getTermsFromJson(json: JValue): List[Term] = {
    (json \\ "items").children.map { x =>
      Term.fromJson(x)
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

  def getTerms(): List[Term] = {
    val jValue = parse(getTermsJson)
    getTermsFromJson(jValue)
  }

  private def callUrl(url: String): String = {
    scala.io.Source.fromURL(url).mkString
  }

}
