package org.caoilte.atomizer.model

import java.io.{ByteArrayOutputStream, StringReader}
import org.caoilte.jaxb.JAXBConverters
import org.joda.time.DateTime
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, FunSuite}

import spray.http.{MediaTypes, MediaType, Uri}

class FeedSerializationTests extends FunSuite with Matchers
with GeneratorDrivenPropertyChecks with JAXBConverters {


  val textTypes = Gen.oneOf[Text.Type](Text.text, Text.html, Text.xhtml)
  val texts:Gen[Text] = for {
    textType <- textTypes
  } yield Text("sample content", textType)
  val someText = texts.map( Some.apply )
  val noText = Gen.const( None:Option[Text] )
  val optTexts = Gen.oneOf( someText, noText )

  val dateTimes = Gen.const( new DateTime().withMillisOfSecond(0) )
  val someDateTime = dateTimes.map ( Some.apply )
  val noneDateTime = Gen.const( None:Option[DateTime] )
  val optDateTimes = Gen.oneOf( someDateTime, noneDateTime )
  val optStrings = Gen.oneOf(None, Some("an id"))

  val sources:Gen[Source] = for {
    id <- optStrings
    title <- optTexts
    updated <- optDateTimes
    rights <- optTexts
  } yield Source(id, title, updated, rights)
  val noSource = Gen.const(None:Option[Source])
  val someSource = sources.map ( Some.apply )
  val optSources = Gen.oneOf( noSource, someSource)

  val uris:Gen[Uri] = Gen.oneOf(Seq(
    Uri("http://example.org/"),
    Uri("http://example.org/2003/12/13/atom03"),
    Uri("/2003/12/13/atom03")
  ))
  val noUri = Gen.const(None:Option[Uri])
  val someUri = uris.map ( Some.apply )
  val optUris = Gen.oneOf( someUri, noUri)

  val relationships = Gen.oneOf[Link.Relationship](Link.alternative, Link.enclosure,
    Link.related, Link.self, Link.via, Link.first, Link.last, Link.previous, Link.next
  )
  val mediaTypes = Gen.oneOf[MediaType](
    MediaTypes.`text/html`,
    MediaTypes.`audio/mpeg`
  )
  val noMediaType = Gen.const(None:Option[MediaType])
  val someMediaType = mediaTypes.map( Some.apply )
  val optMediaTypes = Gen.oneOf( someMediaType, noMediaType)

  val largePositiveLong:Gen[Long] = Gen.choose(0L,10000000L)
  val noLong = Gen.const(None:Option[Long])
  val someLong = largePositiveLong.map( Some.apply )
  val optLongs = Gen.oneOf( someLong, noLong)

  val links:Gen[Link] = for {
    uri <- uris
    relationship <- relationships
    optType <- optMediaTypes
    optString <- optStrings
    optLong <- optLongs
  } yield Link(uri, relationship, optType, optString, optLong)
  val noLink = Gen.const(None:Option[Link])
  val someLink = links.map( Some.apply )
  val optLinks = Gen.oneOf( someLink, noLink)

  val categories:Gen[Category] = for {
    term <- Gen.const("category")
    scheme <- optUris
    label <- optStrings
  } yield Category(term, scheme, label)

  val emails = Gen.const(Email("test@test.com"))
  val noEmail = Gen.const(None:Option[Email])
  val someEmail = emails.map( Some.apply )
  val optEmails = Gen.oneOf( someEmail, noEmail)

  val persons = for {
    name <- Gen.const("name")
    optUri <- optUris
    optEmail <- optEmails
  } yield Person(name, optUri, optEmail)

  val entries = for {
    id <- Gen.const("id")
    title <- texts
    updated <- dateTimes
    content <- optTexts
    link <- optLinks
    summary <- optTexts
    published <- optDateTimes
    source <- optSources
    rights <- optTexts
    authors <- Gen.listOf(persons).map(_.toArray)
    categories <- Gen.listOf(categories).map(_.toArray)
    contributors <- Gen.listOf(persons).map(_.toArray)
  } yield Entry(id, title, updated, content, link, summary, published, source, rights)(authors, categories, contributors)

  val generators = for {
    text <- Gen.const("/myblog.php")
    uri <- optUris
    version <- optStrings
  } yield Generator(text, uri, version)

  val noGenerator = Gen.const(None:Option[Generator])
  val someGenerator = generators.map( Some.apply )
  val optGenerators = Gen.oneOf( someGenerator, noGenerator)

  val feeds = for {
    id <- Gen.const("id")
    title <- texts
    updated <- dateTimes
    link <- optLinks
    generator <- optGenerators
    icon <- optUris
    logo <- optUris
    rights <- optTexts
    subtitle <- optStrings
    entry <- Gen.listOf(entries).map(_.toArray)
    author <- Gen.listOf(persons).map(_.toArray)
    category <- Gen.listOf(categories).map(_.toArray)
    contributor <- Gen.listOf(persons).map(_.toArray)
  } yield Feed(id, title, updated, link, generator, icon, logo, rights, subtitle)(entry, author, category, contributor)

  val XML_FEED =
  """
    |<?xml version="1.0" encoding="utf-8"?>
    |<feed xmlns="http://www.w3.org/2005/Atom">
    |
    |  <title>Example Feed</title>
    |  <link href="http://example.org/"/>
    |  <updated>2003-12-13T18:30:02Z</updated>
    |  <author>
    |    <name>John Doe</name>
    |  </author>
    |  <id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>
    |
    |  <entry>
    |    <title>Atom-Powered Robots Run Amok</title>
    |    <link href="http://example.org/2003/12/13/atom03"/>
    |    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
    |    <updated>2003-12-13T18:30:02Z</updated>
    |    <summary>Some text.</summary>
    |  </entry>
    |
    |</feed>
  """.stripMargin

  val XML_FEED_AS_TYPED_FEED = Atom(Feed(
    id = "urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6",
    title = Text("Example Feed"),
    updated = Atom.dateFormat.parseDateTime("2003-12-13T18:30:02Z")
  )(
      entry = Array(
      Entry (
        title = Text("Atom-Powered Robots Run Amok"),
        link = Some(Link(href = Uri("http://example.org/2003/12/13/atom03"))),
        id = "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a",
        updated = Atom.dateFormat.parseDateTime("2003-12-13T18:30:02Z"),
        summary = Some(Text("Some text."))
      )()
    )
  ))

  test("Generated Texts should serialize and re-serialize correctly") {
    forAll((texts, "text")) { (text: Text) =>
      marshallThenUnmarshall(text) should equal(text)
    }
  }

  test("xml Text without type attribute set should deserialize with type text") {
    val reader = new StringReader("""<?xml version="1.0" encoding="UTF-8"?><text>sample content</text>""")
    val unmarshalledText = unmarshaller.unmarshal(reader)
    unmarshalledText should equal(Text("sample content", Text.text))
  }

  test("Generated Sources should serialize and re-serialize correctly") {
    forAll((sources, "source")) { (source: Source) =>
      marshallThenUnmarshall(source) should equal(source)
    }
  }

  test("A Source without fields defined should not have those fields serialized") {
    val source = Source(None, None, None, None)
    val baos = new ByteArrayOutputStream()
    marshaller.marshal(source,baos)
    baos.toString() should equal("""<?xml version="1.0" encoding="UTF-8"?><source/>""")
  }

  test("Generated Links should serialize and re-serialize correctly") {
    forAll((links, "link")) { (link: Link) =>
      marshallThenUnmarshall(link) should equal(link)
    }
  }

  test("Generated Categories should serialize and re-serialize correctly") {
    forAll((categories, "category")) { (category: Category) =>
      marshallThenUnmarshall(category) should equal(category)
    }
  }

  test("Generated Persons should serialize and re-serialize correctly") {
    forAll((persons, "person")) { (person: Person) =>
      marshallThenUnmarshall(person) should equal(person)
    }
  }

  test("Generated Entries should serialize and re-serialize correctly") {
    forAll((entries, "entry")) { (entry: Entry) =>
      marshallThenUnmarshall(entry) should equal(entry)
    }
  }


  test("Generated Feeds should serialize and re-serialize correctly") {
    forAll((feeds, "feed")) { (feed: Feed) =>
      marshallThenUnmarshall(feed) should equal(feed)
    }
  }

  def marshallThenUnmarshall(obj:AnyRef):AnyRef = {
    val baos = new ByteArrayOutputStream()
    marshaller.marshal(obj, baos)
    val reader = new StringReader(baos.toString)
    val unmarshalledObj:AnyRef = unmarshaller.unmarshal(reader)
    unmarshalledObj
  }

}
