package com.karm.datafeed

import com.karm.model.voting.{Constituency, ElectionSummary, Member, Term}
import com.karm.model.voting.{ElectionSummary, Member, Term}
import org.json4s.{DefaultFormats, JValue}
import org.json4s.jackson.JsonMethods.parse

import scala.annotation.tailrec

object VotingDataFilesDownloader extends App {

  implicit val formats = DefaultFormats

  /* assigned the web calls to values so that I can easily breakpoint + debug */

  /*
  format for a given entity, and the page size parameter
  http://lda.data.parliament.uk/members.json
  http://lda.data.parliament.uk/members.json?_pageSize=5000&_page=0

  As the bandwidth is restricted for the direct calls, you can always web scrape (ugh)
  the page where this is used:
  https://www.parliament.uk/mps-lords-and-offices/mps/
  Honestly may be easier than getting the actual data itself
  */

  // check financial interests here:
  // https://www.parliament.uk/mps-lords-and-offices/standards-and-financial-interests/parliamentary-commissioner-for-standards/registers-of-interests/register-of-members-financial-interests/
  // which leads to something like this:
  // https://publications.parliament.uk/pa/cm/cmregmem/contents1617.htm
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

  //there are a bit less than 115,000 terms; about 22 pages
  //TODO append the rest of the terms - probably don't want to get them all in one hit
  //seems like we can only get up to page 7; 8+ throws a timeout every time
  def getTermsJson(pageNo: Int): String = {
    val json = callUrl(s"http://lda.data.parliament.uk/terms.json?_pageSize=5000&_page=$pageNo")
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

//  lazy val TERMS_PAGE_LIMIT: Int = 22
  lazy val TERMS_PAGE_LIMIT: Int = 7

  @tailrec
  def getTerms(currentPage:Int = 0, terms: List[Term] = Nil): List[Term] = {
    if (currentPage>=TERMS_PAGE_LIMIT){
      getTermsForPage(currentPage) ++ terms
    }
    else{
      val termsForPage:List[Term] = getTermsForPage(currentPage)
      val termsToPass = if (terms.nonEmpty) {
        terms ++ termsForPage
      } else {
        termsForPage
      }
      getTerms(currentPage + 1, termsToPass)
    }
  }

  def getTermsForPage(pageNo: Int): List[Term] = {
      val jValue = parse(getTermsJson(pageNo))
      getTermsFromJson(jValue)
  }

  private def callUrl(url: String): String = {
    scala.io.Source.fromURL(url).mkString
  }

}
