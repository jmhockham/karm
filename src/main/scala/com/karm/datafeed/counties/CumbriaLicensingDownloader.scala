package com.karm.datafeed.counties

object CumbriaLicensingDownloader extends AbstractDataFilesDownloader {

  private val baseSearchUrl = "https://licensing.plymouth.gov.uk/1/LicensingActPremises/Search"
  private val postfixUrlAgrs = "&SearchPremisesWithRepresentationsOnly=false&Column=OPR_NAME&Direction=Ascending"

}
