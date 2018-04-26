package com.karm.utils

import org.apache.commons.logging.{Log, LogFactory}

trait Logger {
  protected implicit lazy val log: Log = LogFactory.getLog(getClass)
}
