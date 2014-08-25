package org.caoilte.jaxb

import javax.xml.bind.annotation.adapters.XmlAdapter

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter


class DateTimeAdapter(dtf: DateTimeFormatter) extends XmlAdapter[String, DateTime] {
  def unmarshal(v: String): DateTime = dtf.parseDateTime(v)
  def marshal(v: DateTime): String = dtf.print(v)
}

class DateTimeOptionAdapter(dtf: DateTimeFormatter) extends CustomOptionAdapter[String,DateTime](new DateTimeAdapter(dtf), null, "")