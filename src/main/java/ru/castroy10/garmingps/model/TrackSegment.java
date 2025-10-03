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
@XmlType(propOrder = {"trkpts"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrackSegment {

    @XmlElement(name = "trkpt", namespace = "http://www.topografix.com/GPX/1/1")
    protected List<TrackPoint> trkpts;

}