@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(type=DateTime.class,
                value=org.caoilte.jaxb.DateTimeAdapter.class)
})
package org.caoilte.atomizer.model;

import org.joda.time.DateTime;
import scala.Option;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;