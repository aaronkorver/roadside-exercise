package com.example.geico.roadside;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class Assistant {
    private UUID id;
    private String name;
    private Geolocation geolocation;

    public Assistant(@NotNull String name) {
        this.name = name;
    }

    public Assistant(UUID id, String name, Geolocation geolocation) {
        this.id = id;
        this.name = name;
        this.geolocation = geolocation;
    }

    public UUID getId() {
        return id;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Assistant{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
