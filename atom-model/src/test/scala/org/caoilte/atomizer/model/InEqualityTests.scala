package org.caoilte.atomizer.model

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, FunSuite}

class InEqualityTests extends FunSuite with Matchers
with GeneratorDrivenPropertyChecks with AtomGenerators {

  implicit override val generatorDrivenConfig =
    PropertyCheckConfig(maxSize = 3)

  val unmatchingEntries:Gen[(Entry, Entry)] = for {
    entry1 <- entries
    arity <- Gen.choose(0,entry1.productArity-1)
    entry2 <- entries suchThat (_.productElement(arity) != entry1.productElement(arity))
  } yield (entry1, entry2)

  test("Two Generated Entries with one different field should not equal eachother") {
    forAll((unmatchingEntries, "entry")) { (entryTuple:(Entry, Entry)) =>
      entryTuple._1 should not equal entryTuple._2
    }
  }

  val unmatchingFeeds:Gen[(Feed, Feed)] = for {
    feed1 <- feeds
    arity <- Gen.choose(0,feed1.productArity-1)
    feed2 <- feeds suchThat (_.productElement(arity) != feed1.productElement(arity))
  } yield (feed1, feed2)

  test("Two Generated Feeds with one different field should not equal each other") {
    forAll((unmatchingFeeds, "feed")) { (feedTuple:(Feed, Feed)) =>
      feedTuple._1 should not equal feedTuple._2
    }
  }
}
