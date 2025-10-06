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

@Service
@Slf4j
public class GpsCreatorService {

    public ResponseEntity<byte[]> createGpxTrack(final List<Coordinate> coordinates) {
        try {
            return ResponseEntity.ok()
                                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"track.gpx\"")
                                 .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                                 .body(getGpsTrack(coordinates));
        } catch (final Exception e) {
            log.error("Ошибка: ", e);
            return ResponseEntity.internalServerError()
                                 .body(printStackTrace(e).getBytes());
        }
    }

    private byte[] getGpsTrack(final List<Coordinate> rawCoordinates) {
        if (CollectionUtils.isEmpty(rawCoordinates)){
            throw new IllegalArgumentException("Должна быть минимум одна пара координат");
        }

        final List<Coordinate> coordinates = rawCoordinates.stream()
                                                .map(this::parseCoordinate)
                                                .toList();

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
        final Route route = new Route("Route маршрута", "Route маршрута", routePoints);

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

    private String printStackTrace(final Throwable e) {
        final StringBuilder sb = new StringBuilder();

        sb.append(e.toString()).append(System.lineSeparator());
        for (final StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString())
              .append(System.lineSeparator());
        }

        Throwable cause = e.getCause();
        while (cause != null) {
            sb.append("Caused by: ")
              .append(cause)
              .append(System.lineSeparator());
            for (final StackTraceElement element : cause.getStackTrace()) {
                sb.append(element.toString())
                  .append(System.lineSeparator());
            }
            cause = cause.getCause();
        }

        return sb.toString();
    }

    private Coordinate parseCoordinate(final Coordinate coordinate) {
        final String latInput = coordinate.lat().trim();
        final double latitude = parseCoordinatePart(latInput);

        final String lonInput = coordinate.lon().trim();
        final double longitude = parseCoordinatePart(lonInput);

        return new Coordinate(
                String.format("%.6f", latitude).replace(',', '.'),
                String.format("%.6f", longitude).replace(',', '.')
        );
    }

    private double parseCoordinatePart(final String input) {
        final String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Неверный формат координаты: " + input);
        }
        final String hemisphereStr = parts[0];
        final String dmsStr = parts[1];
        return getResult(hemisphereStr, dmsStr);
    }

    private double getResult(final String hemisphereStr, final String dmsStr) {
        final char hemisphere = hemisphereStr.charAt(0);

        final String[] dmsParts = dmsStr.split("[°′]");
        if (dmsParts.length < 2) {
            throw new IllegalArgumentException("Неверный формат DMS: " + dmsStr);
        }
        final double degrees = Double.parseDouble(dmsParts[0]);
        final double minutes = Double.parseDouble(dmsParts[1]);
        double result = degrees + minutes / 60.0;

        if (hemisphere == 'S' || hemisphere == 'W') {
            result = -result;
        }
        return result;
    }

}
