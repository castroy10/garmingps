package ru.castroy10.garmingps.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import ru.castroy10.garmingps.model.Extensions;
import ru.castroy10.garmingps.model.GpxFile;
import ru.castroy10.garmingps.model.MetaData;
import ru.castroy10.garmingps.model.Route;
import ru.castroy10.garmingps.model.RoutePoint;
import ru.castroy10.garmingps.model.Track;
import ru.castroy10.garmingps.model.TrackPoint;
import ru.castroy10.garmingps.model.TrackSegment;
import ru.castroy10.garmingps.model.WayPoint;

public class GpsCreator {

    public static void main(final String[] args) {

        final List<Coordinate> coordinates = List.of(
                new Coordinate(60.148500, 30.581917),
                new Coordinate(60.147433, 30.586183),
                new Coordinate(60.145367, 30.582917),
                new Coordinate(60.146433, 30.578650),
                new Coordinate(60.148500, 30.581917)
        );

        // --- Создание Metadata ---
        final MetaData metadata = new MetaData(
                "Круговой маршрут 01-04",
                "Маршрут через точки 01-04 с возвратом в начало",
                "2024-01-15T12:00:00Z"
        );

        // --- Создание Waypoints ---
        final List<WayPoint> waypoints = new ArrayList<>();
        final String[] names = {"01", "02", "03", "04"};
        final String[] symbols = {"Flag, Blue", "Flag, Green", "Flag, Red", "Flag, Yellow"};
        final String[] types = {"Start", "Waypoint", "Waypoint", "Waypoint"};
        final String[] colors = {"FF0000FF", "FF00FF00", "FFFF0000", "FFFFFF00"};

        for (int i = 0; i < coordinates.size() - 1; i++) { // -1, т.к. последняя точка - замыкающая, не добавляем как Waypoint
            final Coordinate coord = coordinates.get(i);
            final Extensions ext = new Extensions(colors[i]);
            waypoints.add(new WayPoint(coord.lat(), coord.lon(), names[i], symbols[i], types[i], ext));
        }
        // Последняя точка (замыкающая) как Waypoint (Finish)
        final Coordinate lastCoord = coordinates.getLast();
        final Extensions lastExt = new Extensions(colors[0]); // Используем цвет первой точки
        waypoints.add(new WayPoint(lastCoord.lat(), lastCoord.lon(), names[0], symbols[0], "Finish", lastExt));

        // --- Создание Route ---
        final List<RoutePoint> routePoints = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            final Coordinate coord = coordinates.get(i);
            final String name = names[i % names.length]; // Используем имя 01, 02, 03, 04, 01
            final String sym = symbols[i % symbols.length]; // Используем символ 01, 02, 03, 04, 01
            final String type = (i == coordinates.size() - 1) ? "Finish" : null; // Тип только для последней точки
            routePoints.add(new RoutePoint(coord.lat(), coord.lon(), name, sym, type));
        }
        final Route route = new Route("Основной маршрут", "Последовательное соединение точек 01-04 с возвратом", routePoints);

        // --- Создание Track ---
        final List<TrackPoint> trackPoints = new ArrayList<>();
        final String baseTime = "2024-01-15T12:00:00Z";
        LocalDateTime time = LocalDateTime.parse(baseTime.replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        for (int i = 0; i < coordinates.size(); i++) {
            final Coordinate coord = coordinates.get(i);
            final String formattedTime = time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
            trackPoints.add(new TrackPoint(coord.lat(), coord.lon())); // ele = 0.0, time из расчёта
            time = time.plusMinutes(5); // Увеличиваем время на 5 минут для следующей точки
        }
        TrackSegment trackSegment = new TrackSegment(trackPoints);
        Track track = new Track("Трек маршрута", trackSegment);

        // --- Создание корневого GPX объекта ---
        final GpxFile gpxFile = new GpxFile(metadata, waypoints, route, track);

        // --- Сериализация в файл ---
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(GpxFile.class);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            marshaller.marshal(gpxFile, new File("detailed_track.gpx"));
            System.out.println("Подробный GPX файл успешно создан: detailed_track.gpx");

        } catch (final JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public record Coordinate(double lat, double lon) {}
}
