package ru.castroy10.garmingps.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "sym", "type"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoutePoint {

    @XmlAttribute(name = "lat", required = true)
    protected double lat;

    @XmlAttribute(name = "lon", required = true)
    protected double lon;

    @XmlElement(name = "name", namespace = "http://www.topografix.com/GPX/1/1")
    protected String name;

    @XmlElement(name = "sym", namespace = "http://www.topografix.com/GPX/1/1")
    protected String sym;

    @XmlElement(name = "type", namespace = "http://www.topografix.com/GPX/1/1")
    protected String type; // Optional

}
