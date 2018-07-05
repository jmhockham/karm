package com.karm.datafeed.counties

import java.net.URL

import scala.xml.NodeSeq

object LondonLicensingDownloader extends AbstractDataFilesDownloader {

  /*

  Seems to create an xml file to "cache" the results of the search; probably gets deleted after a while

  https://www.cityoflondon.gov.uk/MVM/Online/EGov/License_Registers/StdResults.aspx?PT=Licensing%20Act%202003%20and%20Licensing%20Register&SC=Licence%20Type%20is%20LPRM&FT=Licence%20Registers%20Search%20Results&XMLSIDE=/MVM/SiteFiles/OnlineMenus/Licensing/Licensing.xml&XSLTemplate=/mvm/SiteFiles/Skins/M3PP_Online/xslt/Licensing/LicenceRegistersSearchResults.xsl&PS=10&XMLLoc=/MVM/Online/generic/XMLtemp/sivxwwuvyj5buz552n1bve45/e8e6a53e-2864-4f22-8d52-79078e586e59.xml

  ***** Page results *****

  https://www.cityoflondon.gov.uk/MVM/Online/EGov/License_Registers/StdResults.aspx?PT=Licensing%20Act%202003%20and%20Licensing%20Register&PS=10&XMLLoc=/MVM/Online/generic/XMLtemp/sivxwwuvyj5buz552n1bve45/e8e6a53e-2864-4f22-8d52-79078e586e59.xml&FT=Licence%20Registers%20Search%20Results&XSLTemplate=/mvm/SiteFiles/Skins/M3PP_Online/xslt/Licensing/LicenceRegistersSearchResults.xsl&p=0
  https://www.cityoflondon.gov.uk/MVM/Online/EGov/License_Registers/StdResults.aspx?PT=Licensing%20Act%202003%20and%20Licensing%20Register&PS=10&XMLLoc=/MVM/Online/generic/XMLtemp/sivxwwuvyj5buz552n1bve45/e8e6a53e-2864-4f22-8d52-79078e586e59.xml&FT=Licence%20Registers%20Search%20Results&XSLTemplate=/mvm/SiteFiles/Skins/M3PP_Online/xslt/Licensing/LicenceRegistersSearchResults.xsl&p=10
  https://www.cityoflondon.gov.uk/MVM/Online/EGov/License_Registers/StdResults.aspx?PT=Licensing%20Act%202003%20and%20Licensing%20Register&PS=10&XMLLoc=/MVM/Online/generic/XMLtemp/sivxwwuvyj5buz552n1bve45/e8e6a53e-2864-4f22-8d52-79078e586e59.xml&FT=Licence%20Registers%20Search%20Results&XSLTemplate=/mvm/SiteFiles/Skins/M3PP_Online/xslt/Licensing/LicenceRegistersSearchResults.xsl&p=20
   */

  private val baseSearchUrl = "https://www.cityoflondon.gov.uk/MVM/Online/EGov/License_Registers/StdResults.aspx" +
    "?PT=Licensing%20Act%202003%20and%20Licensing%20Register" +
    "&PS=10" +
    "&XMLLoc=/MVM/Online/generic/XMLtemp/sivxwwuvyj5buz552n1bve45/e8e6a53e-2864-4f22-8d52-79078e586e59.xml" +
    "&FT=Licence%20Registers%20Search%20Results" +
    "&XSLTemplate=/mvm/SiteFiles/Skins/M3PP_Online/xslt/Licensing/LicenceRegistersSearchResults.xsl"

  override def getPageData(pageNo: Int = 0): NodeSeq = {
    val urlString = baseSearchUrl + s"&p=$pageNo"
    val url = new URL(urlString)
    xmlFromUrl(url)
  }

  private [counties] def getVenueUrlsFromPageHtml(html: NodeSeq): Seq[String] = {
    (html \\ "td" \ "a").filter(node => (node \@ "href").contains("LicenceRegistersDetails.xsl") ).map(_ \@ "href")
  }

  private [counties] def getVenueNamesFromPageHtml(html: NodeSeq): Seq[String] = {
    (html \\ "td" \ "a").filter(node => (node \@ "href").contains("LicenceRegistersDetails.xsl") ).map(_.text)
  }

}
