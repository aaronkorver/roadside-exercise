package com.example.geico.roadside;

import java.util.StringJoiner;

public class Geolocation {
    double lat;
    double lon;

    public Geolocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Geolocation.class.getSimpleName() + "[", "]")
                .add("lat='" + lat + "'")
                .add("lon='" + lon + "'")
                .toString();
    }
}
