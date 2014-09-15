package org.caoilte.jaxb

import javax.xml.bind.annotation.adapters.XmlAdapter

import spray.http.parser.HttpParser
import spray.http._

class UriAdapter extends XmlAdapter[String, Uri] {
  def unmarshal(v: String): Uri = Uri(v)
  def marshal(v: Uri): String = v.toString()
}
class UriOptionAdapter extends CustomOptionAdapter[String, Uri](new UriAdapter, null, "")

class MediaTypeAdapter extends XmlAdapter[String, MediaType] {
  def unmarshal(contentTypeString: String): MediaType = {

    // Solution given to me on the Spray User List:
    // https://groups.google.com/d/msg/spray-user/8vSTFPQajLM/656ZoXgknv0J
    val contentTypeAsRawHeader = HttpHeaders.RawHeader("Content-Type", contentTypeString)
    val parsedContentTypeHeader = HttpParser.parseHeader(contentTypeAsRawHeader)

    // (This match is exhaustive, but it doesn't look so to the compiler)
    (parsedContentTypeHeader: @unchecked) match {
      case Right(ct: HttpHeaders.`Content-Type`) =>
        ct.contentType.mediaType
      case Left(error: ErrorInfo) =>
        throw new IllegalArgumentException(
          s"Error converting '$contentTypeString' to a MediaType: '${error.summary}'")
    }
  }
  def marshal(v: MediaType): String = v.toString()
}
class MediaTypeOptionAdapter extends CustomOptionAdapter[String,MediaType](new MediaTypeAdapter, null, "")