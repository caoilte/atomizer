package org.caoilte

import java.util.{ArrayList, List => JList}
import javax.xml.bind.annotation.adapters.{XmlAdapter, XmlJavaTypeAdapter}
import javax.xml.bind.annotation.{XmlElementRef, XmlAttribute, XmlElement, XmlValue}

import scala.annotation.meta.field


package object jaxb {
  type xmlElement     = XmlElement@field
  type xmlElementRef      = XmlElementRef @field
  type xmlAttribute     = XmlAttribute@field
  type xmlValue     = XmlValue@field
  type xmlTypeAdapter = XmlJavaTypeAdapter @field

  /**
   * Taken from,
   * https://gist.github.com/krasserm/1891525#file-jaxb-02-scala
   */
  class OptionAdapter[A >: Null](nones: A*) extends XmlAdapter[A, Option[A]] {
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
  class LongAdapter extends XmlAdapter[String, java.lang.Long] {
    override def unmarshal(v: String) = java.lang.Long.parseLong(v)
    override def marshal(v: java.lang.Long) = v.toString
  }
  class LongOptionAdapter extends CustomOptionAdapter[String,java.lang.Long](new LongAdapter,null)

  /**
   * NB As with the CustomOptionAdapter the rather complex type parameters are necessary because of the Moxy bug
   * using the first (B) one (JAXB understood representation) as a source for casting the input on marshalling and the
   * second (L) one for the target. We need the third type parameter (A) to express the contents of our list.
   */
  abstract class AbstractListAdapter[B <: AbstractList[A], L <: List[A], A] extends XmlAdapter[B, List[A]] {
    import scala.collection.JavaConverters._

    def marshal(v: List[A]) = {
      if (v == null) {
        create(new ArrayList[A])
      } else create(v.asJava)
    }
    def unmarshal(v: B) = {
      if (v.elem == null)
        List()
      else
        v.elem.asScala.toList
    }
    def create(l: JList[A]): B
  }

  trait AbstractList[A] {
    def elem: JList[A]
  }
}