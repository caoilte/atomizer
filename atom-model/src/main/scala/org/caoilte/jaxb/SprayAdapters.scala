package org.caoilte.jaxb

import javax.xml.bind.annotation.adapters.XmlAdapter

import spray.http.{MediaTypes, MediaType, Uri}

class UriAdapter extends XmlAdapter[String, Uri] {
  def unmarshal(v: String): Uri = Uri(v)
  def marshal(v: Uri): String = v.toString()
}
class UriOptionAdapter extends CustomOptionAdapter[String,Uri](new UriAdapter, null, "")

class MediaTypeAdapter extends XmlAdapter[String, MediaType] {
  def unmarshal(v: String): MediaType = {
    val splitMediaType = v.split("/")
    if (splitMediaType.length != 2) throw new IllegalArgumentException(s"Invalid media type '$v'")
    val mediaTypeTuple = (splitMediaType(0), splitMediaType(1))

    MediaTypes.getForKey(mediaTypeTuple)
      .getOrElse(throw new IllegalArgumentException(s"Invalid media type '$v'"))
  }
  def marshal(v: MediaType): String = v.toString()
}
class MediaTypeOptionAdapter extends CustomOptionAdapter[String,MediaType](new MediaTypeAdapter, null, "")