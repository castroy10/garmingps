package ru.castroy10.garmingps.model;

public record Coordinate(String lat, String lon, boolean isDecimal) {

    public Coordinate(final String lat, final String lon) {
        this(lat, lon, false);
    }

    public Coordinate(final String lat, final String lon, final boolean isDecimal) {
        this.lat = lat;
        this.lon = lon;
        this.isDecimal = isDecimal;
    }

}