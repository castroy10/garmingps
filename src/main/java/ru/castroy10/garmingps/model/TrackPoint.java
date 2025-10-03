package ru.castroy10.garmingps.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType()
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrackPoint {

    @XmlAttribute(name = "lat", required = true)
    protected double lat;

    @XmlAttribute(name = "lon", required = true)
    protected double lon;

}