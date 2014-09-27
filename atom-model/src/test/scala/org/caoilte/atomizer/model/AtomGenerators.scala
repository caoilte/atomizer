package org.caoilte.atomizer.model

import org.joda.time.DateTime
import org.scalacheck.Gen
import spray.http.{MediaTypes, MediaType, Uri}

trait AtomGenerators {
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
    id <- Gen.oneOf("id", "id2")
    title <- texts
    updated <- dateTimes
    authors <- Gen.listOf(persons).map(_.toArray)
    content <- optTexts
    link <- optLinks
    summary <- optTexts
    categories <- Gen.listOf(categories).map(_.toArray)
    contributors <- Gen.listOf(persons).map(_.toArray)
    published <- optDateTimes
    source <- optSources
    rights <- optTexts
  } yield Entry(id, title, updated, authors, content, link, summary, categories, contributors, published, source, rights)

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
    entry <- Gen.listOf(entries).map(_.toArray)
    author <- Gen.listOf(persons).map(_.toArray)
    link <- optLinks
    category <- Gen.listOf(categories).map(_.toArray)
    contributor <- Gen.listOf(persons).map(_.toArray)
    generator <- optGenerators
    icon <- optUris
    logo <- optUris
    rights <- optTexts
    subtitle <- optStrings
  } yield Feed(id, title, updated, entry, author, link, category, contributor, generator, icon, logo, rights, subtitle)
}
