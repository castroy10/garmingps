package ru.castroy10.garmingps.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.castroy10.garmingps.model.Coordinate;
import ru.castroy10.garmingps.model.Extensions;
import ru.castroy10.garmingps.model.GpxFile;
import ru.castroy10.garmingps.model.MetaData;
import ru.castroy10.garmingps.model.Route;
import ru.castroy10.garmingps.model.RoutePoint;
import ru.castroy10.garmingps.model.Track;
import ru.castroy10.garmingps.model.TrackPoint;
import ru.castroy10.garmingps.model.TrackSegment;
import ru.castroy10.garmingps.model.WayPoint;

@Service
@Slf4j
public class GpsCreatorService {

    public byte[] getGpsTrack(final List<Coordinate> coordinates) {

        final MetaData metadata = new MetaData(
                "Тестовый маршрут",
                "Тестовый маршрут",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME) + "Z"
        );

        final List<WayPoint> waypoints = new ArrayList<>();
        final String[] symbols = {"Flag, Blue", "Flag, Green", "Flag, Red", "Flag, Yellow"};
        final String[] colors = {"FF0000FF", "FF00FF00", "FFFF0000", "FFFFFF00"};

        for (int i = 0; i < coordinates.size() - 1; i++) {
            final Coordinate coord = coordinates.get(i);
            final Extensions ext = new Extensions(colors[(int) (Math.random() * colors.length)]);
            waypoints.add(new WayPoint(coord.lat(), coord.lon(), Integer.toString(i + 1), symbols[(int) (Math.random() * symbols.length)], i == 0 ? "Start" : "Waypoint", ext));
        }

        final Coordinate lastCoord = coordinates.getLast();
        final Extensions lastExt = new Extensions(colors[0]); // Используем цвет первой точки
        waypoints.add(new WayPoint(lastCoord.lat(), lastCoord.lon(), "Finish", symbols[0], "Finish", lastExt));

        final List<RoutePoint> routePoints = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            final Coordinate coord = coordinates.get(i);
            final String name = (i == coordinates.size() - 1) ? "Finish" : Integer.toString(i + 1);
            final String sym = symbols[i % symbols.length];
            final String type = (i == coordinates.size() - 1) ? "Finish" : null;
            routePoints.add(new RoutePoint(coord.lat(), coord.lon(), name, sym, type));
        }
        final Route route = new Route("Основной маршрут", "Основной маршрут", routePoints);

        final List<TrackPoint> trackPoints = new ArrayList<>();
        LocalDateTime time = LocalDateTime.now();
        for (final Coordinate coord : coordinates) {
            time = time.plusMinutes(5).truncatedTo(ChronoUnit.SECONDS);
            trackPoints.add(new TrackPoint(coord.lat(), coord.lon(), "0", time.format(DateTimeFormatter.ISO_DATE_TIME) + "Z"));
        }
        final TrackSegment trackSegment = new TrackSegment(trackPoints);
        final Track track = new Track("Трек маршрута", trackSegment);

        final GpxFile gpxFile = new GpxFile(metadata, waypoints, route, track);

        return getFile(gpxFile);
    }

    private byte[] getFile(final GpxFile gpxFile) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(GpxFile.class);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            marshaller.marshal(gpxFile, byteArrayOutputStream);
            log.info("Подробный GPX файл успешно создан");
            return byteArrayOutputStream.toByteArray();
        } catch (final JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
