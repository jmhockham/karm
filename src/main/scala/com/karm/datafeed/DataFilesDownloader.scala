package com.karm.datafeed

import com.karm.model.{Constituency, ElectionSummary}
import org.json4s.{DefaultFormats, JValue}

object DataFilesDownloader extends App {

  implicit val formats = DefaultFormats

  /* assigned the web calls to values so that I can easily breakpoint + debug */

  /*
  http://lda.data.parliament.uk/members.json
  http://lda.data.parliament.uk/members.json?_pageSize=5000&_page=0


   */

  def getElectionsJson: String = {
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

  def getElectionSummaries(json: JValue) = {
    (json \\ "items").children.map { x =>
      ElectionSummary.fromJson(x)
    }
  }

  def getConstituents(json: JValue) = {
    (json \\ "items").children.map { x =>
      Constituency.fromJson(x)
    }
  }

  private def callUrl(url: String): String = {
    scala.io.Source.fromURL(url).mkString
  }

}
