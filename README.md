# Atomizer

## Atom Model

This module models the [atom-syndication-format] in Scala case classes and uses JAXB annotations to output a clean xml 
representation. It will also support the [atom-pagination-spec] in due course.

Because of a [jaxb-ri-null-marshalling-bug] it has a hard dependency on [eclipse-moxy].

[atom-syndication-format]: http://atomenabled.org/developers/syndication/ "Atom Syndication Format"
[atom-pagination-spec]: http://tools.ietf.org/html/rfc5005#section-3 "Atom Pagination Specification"
[jaxb-ri-null-marshalling-bug]: http://stackoverflow.com/a/11931768/58005 "JAXB Reference Implementation XmlAdapter null marshalling bug"
[eclipse-moxy]: http://www.eclipse.org/eclipselink/moxy.php "EclipseLink MOXy JAXB Implementation"
