package ru.castroy10.garmingps.service;

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
