package com.karm.dao

import com.karm.datafeed.DataFilesDownloader
import org.apache.log4j._
import com.typesafe.scalalogging.Logger

import scala.Predef.ArrowAssoc

object DataInitialiser {

  private val productionLogger = Logger(getClass)

  def init() = {
    productionLogger.debug("Getting election summaries")
    val electionSummaries = DataFilesDownloader.getElectionSummaries()
    productionLogger.debug("Getting constituencies")
    val constituencies = DataFilesDownloader.getConstituents()

    productionLogger.debug("Getting members")
    val members = DataFilesDownloader.getMembers().map { m =>
      val url = m.constituencyUrl
      val constituenciesFiltered = constituencies.filter(_.resourceUrl == url)
      //should only be one constituency
      if(constituenciesFiltered.nonEmpty){
        val constituency = constituenciesFiltered.head
        m.copy(constituency = Some(constituency))
      }
      else{
        productionLogger.debug("couldn't find a constituency!")
        productionLogger.debug("url of missing constituency: "+url)
        m.copy(constituency = None)
      }

    }

    electionSummaries.map(Database.save(_))
    constituencies.map(Database.save(_))
    members.map(Database.save(_))

  }
}
