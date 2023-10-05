package com.example.geico.roadside.postgis;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.locationtech.jts.geom.Point;

import java.util.UUID;

@Entity(name = "assistant")
public class AssistantModel {
    @Id
    private UUID id;
    private String name;
    private double lat;
    private double lon;
    @Column(columnDefinition = "geography")
    private Point location;

    public AssistantModel(UUID id, String name, double lat, double lon, Point location) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.location = location;
    }

    public AssistantModel() {
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public Point getLocation() {
        return location;
    }
}
