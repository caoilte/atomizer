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
import org.joda.time.format.DateTimeFormat
import spray.http.{MediaType, Uri}

import scala.collection.mutable

object Atom {

  val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
  class AtomDateTimeAdapter extends DateTimeAdapter(dateFormat)
  class AtomDateTimeOptionAdapter extends DateTimeOptionAdapter(dateFormat)
}

case class Atom(feed: Feed)

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Feed(id:String,
                title:Text,
                @xmlTypeAdapter(classOf[AtomDateTimeAdapter]) updated: DateTime,
                @xmlTypeAdapter(classOf[LinkOptionAdapter]) link:Option[Link] = None,
                @xmlTypeAdapter(classOf[GeneratorOptionAdapter]) generator: Option[Generator] = None,
                @xmlTypeAdapter(classOf[UriOptionAdapter]) icon:Option[Uri] = None,
                @xmlTypeAdapter(classOf[UriOptionAdapter]) logo:Option[Uri] = None,
                @xmlTypeAdapter(classOf[TextOptionAdapter]) rights:Option[Text] = None,
                @xmlTypeAdapter(classOf[StringOptionAdapter]) subtitle: Option[String] = None
                 )(
                entry:Array[Entry],
                author:Array[Person] = Array[Person](),
                category:Array[Category] = Array[Category](),
                contributor:Array[Person] = Array[Person]()) {
  val xmlns = "http://www.w3.org/2005/Atom"

  private def this() =
    this(
      "", Text(""), new DateTime(), None, None, None, None, None, None
    )(
        Array[Entry](), Array[Person](), Array[Category](), Array[Person]()
      )


  def productArity = 13

  def productElement(n: Int): Any = n match {
    case 0 => id
    case 1 => title
    case 2 => updated
    case 3 => link
    case 4 => generator
    case 5 => icon
    case 6 => logo
    case 7 => rights
    case 8 => subtitle
    case 9 => mutable.WrappedArray.make(entry)
    case 10 => mutable.WrappedArray.make(author)
    case 11 => mutable.WrappedArray.make(category)
    case 12 => mutable.WrappedArray.make(contributor)
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Person(@xmlElement name:String,
                  @xmlElement @xmlTypeAdapter(classOf[UriOptionAdapter]) uri:Option[Uri] = None,
                  @xmlElement @xmlTypeAdapter(classOf[EmailOptionAdapter]) email:Option[Email] = None) {

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
                 @xmlTypeAdapter(classOf[Text.TextOptionAdapter]) content:Option[Text] = None,
                 @xmlTypeAdapter(classOf[LinkOptionAdapter]) link:Option[Link],
                 @xmlTypeAdapter(classOf[Text.TextOptionAdapter]) summary:Option[Text],
                 @xmlTypeAdapter(classOf[AtomDateTimeOptionAdapter]) published:Option[DateTime] = None,
                 @xmlTypeAdapter(classOf[SourceOptionAdapter]) source:Option[Source] = None,
                 @xmlTypeAdapter(classOf[Text.TextOptionAdapter]) rights:Option[Text] = None
  )(
                 val author:Array[Person]= Array[Person](),
                 val category: Array[Category] = Array[Category](),
                 val contributor: Array[Person] = Array[Person]()) {

  private def this() =
    this(
      "", Text(""), new DateTime(), None, None, None, None, None, None
    )(
        Array[Person](), Array[Category](), Array[Person]()
      )

  def productArity = 11

  def productElement(n: Int): Any = n match {
    case 0 => id
    case 1 => title
    case 2 => updated
    case 3 => content
    case 4 => link
    case 5 => summary
    case 6 => published
    case 7 => source
    case 8 => rights
    case 9 => mutable.WrappedArray.make(author)
    case 10 => mutable.WrappedArray.make(category)
    case 11 => mutable.WrappedArray.make(contributor)
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }
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

