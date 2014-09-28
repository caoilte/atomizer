package org.caoilte.atomizer.model

import javax.xml.bind.annotation._
import javax.xml.bind.annotation.adapters._

import org.caoilte.atomizer.model.Atom.{AtomDateTimeAdapter, AtomDateTimeOptionAdapter}
import org.caoilte.atomizer.model.Email.EmailOptionAdapter
import org.caoilte.atomizer.model.Generator.GeneratorOptionAdapter
import org.caoilte.atomizer.model.Link.{LinkOptionAdapter, RelationshipAdapter, Relationship}
import org.caoilte.atomizer.model.Source.SourceOptionAdapter
import org.caoilte.atomizer.model.Text.TextOptionAdapter
import org.caoilte.jaxb._
import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormatterBuilder, DateTimeFormatter}
import spray.http.{MediaType, Uri}

import scala.collection.mutable
import scala.runtime.ScalaRunTime

object Atom {

  /**
   * E.g. 2014-03-30T03:45:00+01:00 or 2014-03-29T03:45:00Z  i.e. yyyy-MM-dd'T'HHmmssZZ OR yyyy-MM-dd'T'HHmmss'Z'
   */
  val outputFormatterWithSecondsAndOptionalTZ: DateTimeFormatter = new DateTimeFormatterBuilder()
    .append(ISODateTimeFormat.dateHourMinuteSecond)
    .appendTimeZoneOffset("Z", true, 2, 4)
    .toFormatter

  class AtomDateTimeAdapter extends DateTimeAdapter(outputFormatterWithSecondsAndOptionalTZ)
  class AtomDateTimeOptionAdapter extends DateTimeOptionAdapter(outputFormatterWithSecondsAndOptionalTZ)
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Feed(id:String,
                title:Text,
                @xmlTypeAdapter(classOf[AtomDateTimeAdapter]) updated: DateTime,
                entry:Array[Entry],
                author:Array[Person] = Array[Person](),
                @xmlTypeAdapter(classOf[LinkOptionAdapter]) link:Option[Link] = None,
                category:Array[Category] = Array[Category](),
                contributor:Array[Person] = Array[Person](),
                @xmlTypeAdapter(classOf[GeneratorOptionAdapter]) generator: Option[Generator] = None,
                @xmlTypeAdapter(classOf[UriOptionAdapter]) icon:Option[Uri] = None,
                @xmlTypeAdapter(classOf[UriOptionAdapter]) logo:Option[Uri] = None,
                @xmlTypeAdapter(classOf[TextOptionAdapter]) rights:Option[Text] = None,
                @xmlTypeAdapter(classOf[StringOptionAdapter]) subtitle: Option[String] = None) {

  private def this() =
    this(
      "", Text(""), new DateTime(), Array[Entry](), Array[Person](), None, Array[Category](), Array[Person](),
      None, None, None, None, None)


  def productArity = 13

  def productElement(n: Int): Any = n match {
    case 0 => id
    case 1 => title
    case 2 => updated
    case 3 => mutable.WrappedArray.make(entry)
    case 4 => mutable.WrappedArray.make(author)
    case 5 => link
    case 6 => mutable.WrappedArray.make(category)
    case 7 => mutable.WrappedArray.make(contributor)
    case 8 => generator
    case 9 => icon
    case 10 => logo
    case 11 => rights
    case 12 => subtitle
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }

  override def equals(that: Any) = ScalaRunTime._equals(this, that)
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Person(name:String,
                  @xmlTypeAdapter(classOf[UriOptionAdapter]) uri:Option[Uri] = None,
                  @xmlTypeAdapter(classOf[EmailOptionAdapter]) email:Option[Email] = None) {

  private def this() = this("", None, None)
}

object Email {

  class EmailOptionAdapter extends OptionAdapter[Email](null, Email(""))
}

@XmlAccessorType(XmlAccessType.FIELD)
case class Email(@xmlValue email:String) {
  override def toString = email

  private def this() = this("")
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

  class RelationshipAdapter extends XmlAdapter[String, Relationship] {
    def marshal(v: Relationship): String = if (v.equals(alternative)) null else v.toString
    def unmarshal(v: String):Relationship = v match {
      case "alternative" => alternative
      case "enclosure" => enclosure
      case "related" => related
      case "self" => self
      case "via" => via
      case "first" => first
      case "last" => last
      case "previous" => previous
      case "next" => next
      case null => alternative
      case other => throw new IllegalArgumentException(s"Invalid relationship '$other'")
    }
  }

  class LinkOptionAdapter extends OptionAdapter[Link](null)
}


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Link(@xmlAttribute @xmlTypeAdapter(classOf[UriAdapter]) href:Uri,
                @xmlAttribute @xmlTypeAdapter(classOf[RelationshipAdapter]) rel:Relationship = Link.alternative,
                @xmlAttribute @xmlTypeAdapter(classOf[MediaTypeOptionAdapter]) `type`:Option[MediaType] = None,
                @xmlAttribute @xmlTypeAdapter(classOf[StringOptionAdapter]) hreflang:Option[String] = None,
                @xmlAttribute @xmlTypeAdapter(classOf[LongOptionAdapter]) length:Option[Long] = None) {

  private def this() = this(Uri(""))
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Category(@xmlAttribute term: String,
                    @xmlAttribute @xmlTypeAdapter(classOf[UriOptionAdapter]) scheme:Option[Uri] = None,
                    @xmlAttribute @xmlTypeAdapter(classOf[StringOptionAdapter]) label:Option[String] = None) {
  private def this() = this("")
}

object Generator {
  class GeneratorOptionAdapter extends OptionAdapter[Generator](null, Generator(""))
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Generator(@xmlValue text:String,
                     @xmlAttribute @xmlTypeAdapter(classOf[UriOptionAdapter]) uri:Option[Uri] = None,
                     @xmlAttribute @xmlTypeAdapter(classOf[StringOptionAdapter]) version:Option[String] = None) {

  private def this() = this("", None, None)
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Entry(id:String,
                 title:Text,
                 @xmlTypeAdapter(classOf[AtomDateTimeAdapter]) updated: DateTime,
                 author:Array[Person]= Array[Person](),
                 @xmlTypeAdapter(classOf[Text.TextOptionAdapter]) content:Option[Text] = None,
                 @xmlTypeAdapter(classOf[LinkOptionAdapter]) link:Option[Link],
                 @xmlTypeAdapter(classOf[Text.TextOptionAdapter]) summary:Option[Text],
                 category: Array[Category] = Array[Category](),
                 contributor: Array[Person] = Array[Person](),
                 @xmlTypeAdapter(classOf[AtomDateTimeOptionAdapter]) published:Option[DateTime] = None,
                 @xmlTypeAdapter(classOf[SourceOptionAdapter]) source:Option[Source] = None,
                 @xmlTypeAdapter(classOf[Text.TextOptionAdapter]) rights:Option[Text] = None) {

  private def this() =
    this("", Text(""), new DateTime(), Array[Person](), None, None, None, Array[Category](), Array[Person](),
      None, None, None)

  def productArity = 12

  def productElement(n: Int): Any = n match {
    case 0 => id
    case 1 => title
    case 2 => updated
    case 3 => mutable.WrappedArray.make(author)
    case 4 => content
    case 5 => link
    case 6 => summary
    case 7 => mutable.WrappedArray.make(category)
    case 8 => mutable.WrappedArray.make(contributor)
    case 9 => published
    case 10 => source
    case 11 => rights
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }

  override def equals(that: Any) = ScalaRunTime._equals(this, that)
}

object Source {
  class SourceOptionAdapter extends OptionAdapter[Source](null)
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Source(@xmlTypeAdapter(classOf[StringOptionAdapter]) id:Option[String],
                  @xmlTypeAdapter(classOf[TextOptionAdapter]) title:Option[Text],
                  @xmlTypeAdapter(classOf[AtomDateTimeOptionAdapter]) updated: Option[DateTime],
                  @xmlTypeAdapter(classOf[TextOptionAdapter]) rights:Option[Text] = None) {

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
  class TypeOptionAdapter extends CustomOptionAdapter[String, Type](new TypeAdapter)

  class TextOptionAdapter extends OptionAdapter[Text](null, Text(""))
}
trait Content


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Text(@xmlValue content:String,
                @xmlAttribute @xmlTypeAdapter(classOf[Text.TypeAdapter]) `type`:Text.Type = Text.text) extends Content {
  private def this() = this("", Text.text)
}

case class ContentLink(src:Uri, `type`:MediaType)