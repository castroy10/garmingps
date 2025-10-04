package ru.castroy10.garmingps.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "gpx", namespace = "http://www.topografix.com/GPX/1/1")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"metadata", "wpt", "rte", "trk"})
@NoArgsConstructor
@Getter
@Setter
public class GpxFile {

    @XmlAttribute(name = "version", required = true)
    protected String version = "1.1";

    @XmlAttribute(name = "creator", required = true)
    protected String creator = "GPX Route Generator";

    @XmlElement(name = "metadata", namespace = "http://www.topografix.com/GPX/1/1")
    protected MetaData metadata;

    @XmlElement(name = "wpt", namespace = "http://www.topografix.com/GPX/1/1")
    protected List<WayPoint> wpt;

    @XmlElement(name = "rte", namespace = "http://www.topografix.com/GPX/1/1")
    protected Route rte;

    @XmlElement(name = "trk", namespace = "http://www.topografix.com/GPX/1/1")
    protected Track trk;

    public GpxFile(final Track trk) {
        this.trk = trk;
    }

    public GpxFile(final MetaData metadata, final List<WayPoint> wpt, final Route rte, final Track trk) {
        this.metadata = metadata;
        this.wpt = wpt;
        this.rte = rte;
        this.trk = trk;
    }

}
