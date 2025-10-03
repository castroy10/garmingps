package ru.castroy10.garmingps.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "trkseg"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Track {

    @XmlElement(name = "name", namespace = "http://www.topografix.com/GPX/1/1")
    protected String name;

    @XmlElement(name = "trkseg", namespace = "http://www.topografix.com/GPX/1/1")
    protected TrackSegment trkseg;

}