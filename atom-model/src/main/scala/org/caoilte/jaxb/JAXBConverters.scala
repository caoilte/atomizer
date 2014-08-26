package org.caoilte.jaxb

import javax.xml.bind.{JAXBContext, Marshaller, Unmarshaller}

import org.caoilte.atomizer.model._

/**
 * On threadsafety:
 * - https://jaxb.java.net/guide/Performance_and_thread_safety.html
 * - http://stackoverflow.com/questions/7400422/jaxb-creating-context-and-marshallers-cost
 */
trait JAXBConverters {

  val context:JAXBContext = JAXBContext.newInstance(classOf[Text], classOf[Source], classOf[Link],
    classOf[Category], classOf[Person], classOf[Entry])

  // TODO: pool
  def marshaller:Marshaller = {
    val marshaller = context.createMarshaller()
    // Completely remove the xml namespacing node (thus making our serialized object a 'fragment')
    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true)
    // Now add back in the xml namespacing node but without the annoying 'standalone="yes"' attribute
    // Details here: http://stackoverflow.com/questions/277996/jaxb-remove-standalone-yes-from-generated-xml
    marshaller.setProperty(
      //"com.sun.xml.internal.bind.xmlHeaders", // required if jaxb.properties has JDK RI set
      "com.sun.xml.bind.xmlHeaders",
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
    marshaller
  }

  // TODO: pool
  def unmarshaller:Unmarshaller = {
    context.createUnmarshaller
  }

}
