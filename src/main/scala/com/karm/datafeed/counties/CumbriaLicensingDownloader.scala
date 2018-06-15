package com.karm.datafeed.counties

object CumbriaLicensingDownloader extends AbstractDataFilesDownloader {

  private val baseSearchUrl = "https://licensing.plymouth.gov.uk/1/LicensingActPremises/Search"
  private val postfixUrlAgrs = "&SearchPremisesWithRepresentationsOnly=false&Column=OPR_NAME&Direction=Ascending"

  /*
  POST request

  http://applications.southlakeland.gov.uk/LicensingRegister/Licensing/Default/Premises

  ctl00$BodyMainContentPlaceHolder$SearchLicenceType: L15
  ctl00$BodyMainContentPlaceHolder$SearchType: NPA

  curl -d "ctl00$BodyMainContentPlaceHolder$SearchLicenceType=L15&ctl00$BodyMainContentPlaceHolder$SearchType=NPA" -X POST http://applications.southlakeland.gov.uk/LicensingRegister/Licensing/Default/Premises

   */

}
