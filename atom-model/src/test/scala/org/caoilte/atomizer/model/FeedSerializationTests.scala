package org.caoilte.atomizer.model

import java.io.{ByteArrayOutputStream, StringReader}
import org.caoilte.jaxb.JAXBConverters
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, FunSuite}

import spray.http.Uri

import scala.xml.XML

class FeedSerializationTests extends FunSuite with Matchers
with GeneratorDrivenPropertyChecks with JAXBConverters with AtomGenerators {

  implicit override val generatorDrivenConfig =
    PropertyCheckConfig(maxSize = 3)

  val XML_FEED =
  """<?xml version="1.0" encoding="utf-8"?>
    |<feed>
    |
    |  <id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>
    |  <title>Example Feed</title>
    |  <updated>2003-12-13T18:30:02Z</updated>
    |
    |  <entry>
    |    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
    |    <title>Atom-Powered Robots Run Amok</title>
    |    <updated>2003-12-13T18:30:02Z</updated>
    |    <link href="http://example.org/2003/12/13/atom03"/>
    |    <summary>Some text.</summary>
    |  </entry>
    |
    |  <author>
    |    <name>John Doe</name>
    |  </author>
    |  <link href="http://example.org/"/>
    |
    |</feed>
  """.stripMargin

  val XML_FEED_AS_TYPED_FEED = Feed(
    title = Text("Example Feed"),
    link = Some(Link(Uri("http://example.org/"))),
    updated = Atom.outputFormatterWithSecondsAndOptionalTZ.parseDateTime("2003-12-13T18:30:02Z"),
    author = Array(Person("John Doe")),
    id = "urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6",
    entry = Array(
      Entry (
        title = Text("Atom-Powered Robots Run Amok"),
        link = Some(Link(href = Uri("http://example.org/2003/12/13/atom03"))),
        id = "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a",
        updated = Atom.outputFormatterWithSecondsAndOptionalTZ.parseDateTime("2003-12-13T18:30:02Z"),
        summary = Some(Text("Some text."))
      )
    )
  )

  test("Example XML Feed should de-serialize to example Typed Feed") {
    val reader = new StringReader(XML_FEED)
    val unmarshalledObj:AnyRef = unmarshaller.unmarshal(reader)
    unmarshalledObj should equal(XML_FEED_AS_TYPED_FEED)
  }

  test("Example Typed Feed should serialize to expected XML Feed") {
    import org.scalatest.StreamlinedXmlEquality._

    val baos = new ByteArrayOutputStream()
    marshaller.marshal(XML_FEED_AS_TYPED_FEED, baos)
    XML.loadString(baos.toString) should equal(XML.loadString(XML_FEED))
  }

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
