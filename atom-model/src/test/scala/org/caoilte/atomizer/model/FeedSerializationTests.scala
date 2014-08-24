package org.caoilte.atomizer.model

import java.io.{ByteArrayOutputStream, StringReader}
import org.caoilte.jaxb.JAXBConverters
import org.joda.time.DateTime
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, FunSuite}

import spray.http.Uri

class FeedSerializationTests extends FunSuite with Matchers
with GeneratorDrivenPropertyChecks with JAXBConverters {


  val textTypes = Gen.oneOf[Text.Type](Text.text, Text.html, Text.xhtml)
  val texts:Gen[Text] = for {
    textType <- textTypes
  } yield Text("sample content", textType)
  val someText = texts.map( Some.apply )
  val noText = Gen.const( None:Option[Text] )
  val optTexts = Gen.oneOf( someText, noText )

  val optionalDateTimes = Gen.oneOf[Option[DateTime]](Some(new DateTime().withMillisOfSecond(0)), None)

  val sources:Gen[Source] = for {
    id <- Gen.oneOf(None, Some("an id"))
    title <- optTexts
    updated <- optionalDateTimes
    rights <- optTexts
  } yield Source(id, title, updated, rights)

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
    updated = Atom.dateFormat.parseDateTime("2003-12-13T18:30:02Z"),
    entries = List(
      Entry (
        title = Text("Atom-Powered Robots Run Amok"),
        link = Some(Link(href = Uri("http://example.org/2003/12/13/atom03"))),
        id = "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a",
        updated = Atom.dateFormat.parseDateTime("2003-12-13T18:30:02Z"),
        summary = Some(Text("Some text."))
      )
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

  def marshallThenUnmarshall(obj:AnyRef):AnyRef = {
    val baos = new ByteArrayOutputStream()
    marshaller.marshal(obj, baos)
    val reader = new StringReader(baos.toString)
    val unmarshalledObj:AnyRef = unmarshaller.unmarshal(reader)
    unmarshalledObj
  }

}
