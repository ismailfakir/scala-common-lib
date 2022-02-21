package io.github.ismailfakir.scalacommon.time

import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern

import java.time._
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {

  val defaultZoneId: ZoneId = ZoneId.of("UTC+1")

  val defaultDateTimeformatter: DateTimeFormatter =  DateTimeFormatter.ISO_ZONED_DATE_TIME

  def now(zoneId: ZoneId = defaultZoneId): ZonedDateTime = ZonedDateTime.now(zoneId)

  def parseZonedDateTime(dateStr: String, formatter: DateTimeFormatter = defaultDateTimeformatter): ZonedDateTime = {
    ZonedDateTime.parse(dateStr, formatter)
  }

  def zonedDateTimeToString(date: ZonedDateTime, formatter: DateTimeFormatter = defaultDateTimeformatter): String = {
    date.format(formatter)
  }

  def dateTimeFormatter(pattern: String): DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
  def zoneId(zone: String): ZoneId = ZoneId.of(zone)

}
