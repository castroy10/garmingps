package ru.castroy10.garmingps.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "desc", "rtepts"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Route {

    @XmlElement(name = "name", namespace = "http://www.topografix.com/GPX/1/1")
    protected String name;

    @XmlElement(name = "desc", namespace = "http://www.topografix.com/GPX/1/1")
    protected String desc;

    @XmlElement(name = "rtept", namespace = "http://www.topografix.com/GPX/1/1")
    protected List<RoutePoint> rtepts;

}
