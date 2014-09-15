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
    marshaller
  }

  // TODO: pool
  def unmarshaller:Unmarshaller = {
    context.createUnmarshaller
  }

}
