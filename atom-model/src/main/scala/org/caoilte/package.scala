package org.caoilte

import javax.xml.bind.annotation.adapters.{XmlAdapter, XmlJavaTypeAdapter}
import javax.xml.bind.annotation.{XmlAttribute, XmlElement, XmlValue}

import scala.annotation.meta.field


package object jaxb {
  type xmlElement     = XmlElement@field
  type xmlAttribute     = XmlAttribute@field
  type xmlValue     = XmlValue@field
  type xmlTypeAdapter = XmlJavaTypeAdapter @field

  /**
   * Taken from,
   * https://gist.github.com/krasserm/1891525#file-jaxb-02-scala
   */
  class OptionAdapter[A](nones: A*) extends XmlAdapter[A, Option[A]] {
    def marshal(v: Option[A]): A = {
      v.getOrElse(nones(0))
    }
    def unmarshal(v: A) = {
      if (nones contains v) None else Some(v)
    }
  }

  /**
   * NB The double type parameters ([S,A]) are necessary because Moxy always uses the first one as a target for casting
   * from the input string before passing to unmarshal. Unfortunately this means you need to always pass String as
   * the first type parameter when extending the CustomOptionAdapter (eg see DateTimeOptionAdapter).
   * - (we use Moxy because of http://stackoverflow.com/a/11931768/58005)
   */
  class CustomOptionAdapter[S,A](customAdapter:XmlAdapter[String,A], nones: String*) extends XmlAdapter[String, Option[A]] {
    def marshal(v: Option[A]): String = {
      v.map(customAdapter.marshal).getOrElse(nones(0))
    }
    def unmarshal(v: String):Option[A] = {
      if (nones contains v) None else Some(customAdapter.unmarshal(v))
    }
  }

  /**
   * Taken from,
   * https://gist.github.com/krasserm/1891525#file-jaxb-02-scala
   * (Nones swapped around because of
   */
  class StringOptionAdapter extends OptionAdapter[String](null, "")
}