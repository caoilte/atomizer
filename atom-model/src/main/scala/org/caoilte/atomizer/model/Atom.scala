package org.caoilte.atomizer.model

import javax.xml.bind.annotation._
import javax.xml.bind.annotation.adapters._

import org.caoilte.atomizer.model.Atom.AtomDateTimeOptionAdapter
import org.caoilte.atomizer.model.Link.Relationship
import org.caoilte.atomizer.model.Text.OptionTextAdapter
import org.caoilte.jaxb._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import spray.http.{MediaType, Uri}

object Atom {

  val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
  class AtomDateTimeOptionAdapter extends DateTimeOptionAdapter(dateFormat)
}

case class Atom(feed: Feed)

@XmlRootElement()
case class Feed(id:String, title:Text, updated: DateTime, authors:List[Person] = List(),
                link:Option[Link] = None, category: List[Category] = List(), contributors: List[Person] = List(),
                 generator: Option[Generator] = None, icon:Option[Uri] = None, logo:Option[Uri] = None,
                 rights:Option[Text] = None, subtitle: Option[String] = None, entries: List[Entry]) {
  val xmlns = "http://www.w3.org/2005/Atom"

}

case class Person(name:String, uri:Option[Uri] = None, email:Option[Email] = None)

case class Email(email:String) {
  override def toString = email
}

object Link {
  sealed trait Relationship
  case object alternative extends Relationship
  case object enclosure extends Relationship
  case object related extends Relationship
  case object self extends Relationship
  case object via extends Relationship
  case object first extends Relationship
  case object last extends Relationship
  case object previous extends Relationship
  case object next extends Relationship
}

case class Link(href:Uri, rel:Relationship = Link.alternative, `type`:Option[MediaType] = None,
                hreflang:Option[String] = None, length:Option[Long] = None)

case class Category(term: String, scheme:Option[Uri] = None, label:Option[String] = None)

case class Generator(text:String, uri:Option[Uri] = None, version:Option[String] = None)

case class Entry(id:String, title:Text, updated: DateTime, authors:List[Person]= List(),
                  content:Option[Content] = None, link:Option[Link], summary:Option[Text], category: List[Category] = List(),
                  contributors: List[Person] = List(), published:Option[DateTime] = None, source:Option[Source] = None,
                  rights:Option[Text] = None)

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Source(@xmlElement @xmlTypeAdapter(classOf[StringOptionAdapter]) id:Option[String],
                  @xmlElement @xmlTypeAdapter(classOf[OptionTextAdapter]) title:Option[Text],
                  @xmlElement @xmlTypeAdapter(classOf[AtomDateTimeOptionAdapter]) updated: Option[DateTime],
                  @xmlElement @xmlTypeAdapter(classOf[OptionTextAdapter]) rights:Option[Text] = None) {

  private def this() = this(None, None, None, None)
}

object Text {

  sealed trait Type
  case object text extends Type
  case object html extends Type
  case object xhtml extends Type

  class TypeAdapter extends XmlAdapter[String, Type] {
    def marshal(v: Type): String = if (v.equals(text)) null else v.toString
    def unmarshal(v: String):Type = v match {
      case "text" => text
      case "html" => html
      case "xhtml" => xhtml
      case null => text
      case other => throw new IllegalArgumentException(s"Invalid type '$other'")
    }
  }


  class OptionTypeAdapter extends XmlAdapter[String, Option[Type]] {
    def marshal(v: Option[Type]): String = v.map(_.toString).orNull
    def unmarshal(v: String):Option[Type] = {
      println("fup '"+v+";")
      v match {
        case "text" => Some(text)
        case "html" => Some(html)
        case "xhtml" => Some(xhtml)
        case null => Some(text)
        case other => throw new IllegalArgumentException(s"Invalid type '$other'")
      }
    }
  }
  class OptionTextAdapter extends OptionAdapter[Text](null, Text(""))
}
trait Content


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Text(@xmlValue content:String,
                @xmlAttribute @xmlTypeAdapter(classOf[Text.TypeAdapter]) `type`:Text.Type = Text.text) {
  private def this() = this("", Text.text)
}

case class ContentLink(src:Uri, `type`:MediaType)

