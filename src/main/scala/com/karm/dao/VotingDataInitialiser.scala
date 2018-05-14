package com.karm.dao

import com.karm.datafeed.VotingDataFilesDownloader
import com.karm.model.voting.Term
import com.karm.model.voting.{Member, Term}
import org.apache.log4j._
import com.typesafe.scalalogging.Logger

import scala.Predef.ArrowAssoc

object VotingDataInitialiser {

  private val productionLogger = Logger(getClass)

  def init() = {
    productionLogger.debug("Persisting election summaries")
    val electionSummaries = VotingDataFilesDownloader.getElectionSummaries()
    electionSummaries.map(Database.save(_))

    productionLogger.debug("Persisting constituencies")
    val constituencies = VotingDataFilesDownloader.getConstituents()
    val savedConstituencies = constituencies.map(Database.save(_))

    productionLogger.debug("Persisting members")
    val members = VotingDataFilesDownloader.getMembers()
    val membersOfCommons: List[Member] = members.filter(!_.isInLords).map { m =>
      val url = m.constituencyUrl
      val constituenciesFiltered = savedConstituencies.filter(_.resourceUrl == url)
      //should only be one constituency
      if(constituenciesFiltered.nonEmpty){
        val constituency = constituenciesFiltered.head
        m.copy(constituency = Some(constituency))
      }
      else {
        m.copy(constituency = None)
      }
    }

    val membersOfLords = members.filter(_.isInLords)

    membersOfCommons.map(Database.save(_))
    membersOfLords.map(Database.save(_))

    //terms can be nested, so need to check broader/exact children (this is a restriction of SORM)
    productionLogger.debug("Persisting terms")
    val terms = VotingDataFilesDownloader.getTerms()
    terms.map { term =>
      val possibleExactTerm: Option[Term] = term.exactTermJson.map { json =>
        Database.save(Term.fromJson(json))
      }
      val possibleBroaderTerm: Option[Term] = term.broaderTermJson.map { json =>
        Database.save(Term.fromJson(json))
      }
      term.copy(exactTerm = possibleExactTerm, broaderTerm = possibleBroaderTerm)
    }
    terms.map(Database.save(_))
  }
}
