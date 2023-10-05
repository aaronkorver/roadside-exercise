package com.example.geico.roadside.postgis;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssistantRepository extends JpaRepository<AssistantModel, UUID> {

    Optional<AssistantModel> findAssistantModelByName(String name);
    @Query(value="select *, location <-> :p as dist from assistant order by dist limit :limit", nativeQuery = true)
    List<AssistantModel> findNearest(Point p,int limit);
}
