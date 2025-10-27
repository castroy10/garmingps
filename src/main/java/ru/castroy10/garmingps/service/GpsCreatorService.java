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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
import ru.castroy10.garmingps.model.enums.Color;
import ru.castroy10.garmingps.model.enums.Flag;

@Service
@Slf4j
@RequiredArgsConstructor
public class GpsCreatorService {

    private final ParseCoordinateService parseCoordinateService;
    private static final String START = "Start";
    private static final String FINISH = "Finish";
    private static final String WAYPOINT = "Waypoint";
    private static final String DIRECTION = "Маршрут";
    private static final String ROUTE = "Route маршрута";
    private static final String TRACK = "Трек маршрута";

    public ResponseEntity<byte[]> createGpxTrack(final List<Coordinate> coordinates) {
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"track.gpx\"")
                             .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                             .body(getGpsTrack(coordinates));
    }

    public ResponseEntity<byte[]> createGpxTrackFromText(final List<String> text) {
        final List<Coordinate> coordinateList = parseCoordinateService.parseCoordinateFromText(text);
        return createGpxTrack(coordinateList);
    }

    private byte[] getGpsTrack(final List<Coordinate> rawCoordinates) {
        if (CollectionUtils.isEmpty(rawCoordinates)) {
            throw new IllegalArgumentException("Должна быть минимум одна пара координат");
        }
        final List<Coordinate> coordinates = rawCoordinates.stream()
                                                           .map(parseCoordinateService::parseCoordinate)
                                                           .toList();
        final MetaData metadata = new MetaData(
                DIRECTION,
                DIRECTION,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME) + "Z"
        );

        final List<WayPoint> waypoints = new ArrayList<>();
        for (int i = 0; i < coordinates.size() - 1; i++) {
            final Coordinate coord = coordinates.get(i);
            waypoints.add(new WayPoint(coord.lat(), coord.lon(), Integer.toString(i + 1), Flag.GREEN.getCode(),
                                       i == 0 ? START : WAYPOINT, null));
        }

        final Coordinate lastCoord = coordinates.getLast();
        waypoints.add(new WayPoint(lastCoord.lat(), lastCoord.lon(), FINISH, Flag.GREEN.getCode(), FINISH, null));

        final List<RoutePoint> routePoints = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            final Coordinate coord = coordinates.get(i);
            final String name = (i == coordinates.size() - 1) ? FINISH : Integer.toString(i + 1);
            final String sym = Flag.GREEN.getCode();
            final String type = (i == coordinates.size() - 1) ? FINISH : null;
            routePoints.add(new RoutePoint(coord.lat(), coord.lon(), name, sym, type));
        }
        final Route route = new Route(ROUTE, ROUTE, routePoints);

        final List<TrackPoint> trackPoints = new ArrayList<>();
        LocalDateTime time = LocalDateTime.now();
        for (final Coordinate coord : coordinates) {
            time = time.plusMinutes(5).truncatedTo(ChronoUnit.SECONDS);
            trackPoints.add(new TrackPoint(coord.lat(), coord.lon(), "0", time.format(DateTimeFormatter.ISO_DATE_TIME) + "Z"));
        }
        final TrackSegment trackSegment = new TrackSegment(trackPoints);
        final Track track = new Track(TRACK, trackSegment);
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
            log.error("Ошибка маршализации GPX файла");
            throw new RuntimeException(e);
        }
    }

}
