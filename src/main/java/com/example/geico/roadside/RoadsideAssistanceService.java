package com.example.geico.roadside;

import java.util.List;
import java.util.Optional;

public interface RoadsideAssistanceService {

    void updateAssistantLocation(Assistant assistant, Geolocation assistantLocation);

    List<Assistant> findNearestAssistants(Geolocation geolocation, int limit);

    Optional<Assistant> reserveAssistant(Customer customer, Geolocation geolocation);

    void releaseAssistant(Customer customer, Assistant assistant);
}
