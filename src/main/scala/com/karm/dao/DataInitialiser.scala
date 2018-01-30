package com.karm.dao

import com.karm.datafeed.DataFilesDownloader
import com.karm.model.Member
import org.apache.log4j._
import com.typesafe.scalalogging.Logger

import scala.Predef.ArrowAssoc

object DataInitialiser {

  private val productionLogger = Logger(getClass)

  def init() = {
    productionLogger.debug("Persisting election summaries")
    val electionSummaries = DataFilesDownloader.getElectionSummaries()
    electionSummaries.map(Database.save(_))

    productionLogger.debug("Persisting constituencies")
    val constituencies = DataFilesDownloader.getConstituents()
    val savedConstituencies = constituencies.map(Database.save(_))

    productionLogger.debug("Persisting members")
    val members = DataFilesDownloader.getMembers()
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
  }
}
