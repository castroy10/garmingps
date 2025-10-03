package ru.castroy10.garmingps.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "desc", "time"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MetaData {

    @XmlElement(name = "name", namespace = "http://www.topografix.com/GPX/1/1")
    protected String name;

    @XmlElement(name = "desc", namespace = "http://www.topografix.com/GPX/1/1")
    protected String desc;

    @XmlElement(name = "time", namespace = "http://www.topografix.com/GPX/1/1")
    @XmlSchemaType(name = "dateTime")
    protected String time;
}
