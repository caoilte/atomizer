package org.caoilte.atomizer.model

import javax.xml.bind.annotation._
import javax.xml.bind.annotation.adapters._

import org.caoilte.atomizer.model.Atom.{AtomDateTimeAdapter, AtomDateTimeOptionAdapter}
import org.caoilte.atomizer.model.Email.EmailOptionAdapter
import org.caoilte.atomizer.model.Link.{LinkOptionAdapter, RelationshipAdapter, Relationship}
import org.caoilte.atomizer.model.Source.SourceOptionAdapter
import org.caoilte.atomizer.model.Text.TextOptionAdapter
import org.caoilte.jaxb._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import spray.http.{MediaType, Uri}

object Atom {

  val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
  class AtomDateTimeAdapter extends DateTimeAdapter(dateFormat)
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

object Person {
  import java.util.{List => JList}
  import scala.collection.JavaConverters._

  class PersonsAdapter extends AbstractListAdapter[PersonItems,List[Person],Person] {
    def create(l: JList[Person]) = new PersonItems(l)
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  case class PersonItems(@xmlElementRef(name = "person") elem: JList[Person]) extends AbstractList[Person] {
    def this() = this(null)
  }
  object PersonItems {
    def apply(l: Iterable[Person]) = new PersonItems(l.toList.asJava)
  }
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Person(@xmlElement name:String,
                  @xmlElement @xmlTypeAdapter(classOf[UriOptionAdapter]) uri:Option[Uri] = None,
                  @xmlElement @xmlTypeAdapter(classOf[EmailOptionAdapter]) email:Option[Email] = None) {

  private def this() = this("")
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

object Category {
  import java.util.{List => JList}
  import scala.collection.JavaConverters._

  class CategoriesAdapter extends AbstractListAdapter[CategoryItems,List[Category],Category] {
    def create(l: JList[Category]) = new CategoryItems(l)
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  case class CategoryItems(@xmlElementRef(name = "category") elem: JList[Category]) extends AbstractList[Category] {
    def this() = this(null)
  }
  object CategoryItems {
    def apply(l: Iterable[Category]) = new CategoryItems(l.toList.asJava)
  }
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Category(@xmlAttribute term: String,
                    @xmlAttribute @xmlTypeAdapter(classOf[UriOptionAdapter]) scheme:Option[Uri] = None,
                    @xmlAttribute @xmlTypeAdapter(classOf[StringOptionAdapter]) label:Option[String] = None) {
  private def this() = this("")
}

case class Generator(text:String, uri:Option[Uri] = None, version:Option[String] = None)

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Entry(@xmlElement id:String,
                 @xmlElement title:Text,
                 @xmlElement @xmlTypeAdapter(classOf[AtomDateTimeAdapter]) updated: DateTime,
                 @xmlTypeAdapter(classOf[Person.PersonsAdapter]) author:List[Person]= List(),
                 @xmlElement @xmlTypeAdapter(classOf[Text.TextOptionAdapter]) content:Option[Text] = None,
                 @xmlElement @xmlTypeAdapter(classOf[LinkOptionAdapter]) link:Option[Link],
                 @xmlElement @xmlTypeAdapter(classOf[Text.TextOptionAdapter]) summary:Option[Text],
                 @xmlTypeAdapter(classOf[Category.CategoriesAdapter]) category: List[Category] = List(),
                 @xmlTypeAdapter(classOf[Person.PersonsAdapter]) contributor: List[Person] = List(),
                 @xmlElement @xmlTypeAdapter(classOf[AtomDateTimeOptionAdapter]) published:Option[DateTime] = None,
                 @xmlElement @xmlTypeAdapter(classOf[SourceOptionAdapter]) source:Option[Source] = None,
                 @xmlElement @xmlTypeAdapter(classOf[Text.TextOptionAdapter]) rights:Option[Text] = None) {


  private def this() = this("", Text(""), new DateTime(), List(), None, None, None, List(), List(), None, None, None)
}

object Source {

  class SourceOptionAdapter extends OptionAdapter[Source](null)
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
case class Source(@xmlElement @xmlTypeAdapter(classOf[StringOptionAdapter]) id:Option[String],
                  @xmlElement @xmlTypeAdapter(classOf[TextOptionAdapter]) title:Option[Text],
                  @xmlElement @xmlTypeAdapter(classOf[AtomDateTimeOptionAdapter]) updated: Option[DateTime],
                  @xmlElement @xmlTypeAdapter(classOf[TextOptionAdapter]) rights:Option[Text] = None) {

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

