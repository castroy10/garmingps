package ru.castroy10.garmingps.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import ru.castroy10.garmingps.model.Coordinate;

@Service
public class ParseCoordinateService {

    public Coordinate parseCoordinate(final Coordinate coordinate) {
        final String latInput = coordinate.lat().trim();
        final double latitude = parseCoordinatePart(latInput);

        final String lonInput = coordinate.lon().trim();
        final double longitude = parseCoordinatePart(lonInput);

        return new Coordinate(
                String.format("%.6f", latitude).replace(',', '.'),
                String.format("%.6f", longitude).replace(',', '.')
        );
    }

    public List<Coordinate> parseCoordinateFromText(final List<String> text) {
        return text.stream()
                   .skip(1)
                   .map(string -> {
                       final String[] stringsArray = string.split(" ");
                       try {
                           return new Coordinate(stringsArray[1] + " " + stringsArray[2],
                                                 stringsArray[3] + " " + stringsArray[4]);
                       } catch (final ArrayIndexOutOfBoundsException e) {
                           throw new IllegalArgumentException("Неверный формат координаты: " + string);
                       }
                   })
                   .toList();
    }

    public List<Coordinate> parseCoordinateFromText(final String text) {
        final List<String> coordinatesList = new ArrayList<>();
        final String[] lines = text.split("\\r?\\n");
        for (final String line : lines) {
            final String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty()) {
                coordinatesList.add(trimmedLine);
            }
        }
        return parseCoordinateFromText(coordinatesList);
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
            throw new IllegalArgumentException("Неверный формат части координаты: " + dmsStr);
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
