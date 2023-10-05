package com.example.geico.roadside;

import java.util.UUID;

public class Customer {

    UUID id;
    String name;

    public Customer(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
