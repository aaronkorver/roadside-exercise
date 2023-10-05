package com.example.geico.roadside.postgis;

import com.example.geico.roadside.Assistant;
import com.example.geico.roadside.Customer;
import com.example.geico.roadside.Geolocation;
import com.example.geico.roadside.stateMachine.AssistantState;
import com.example.geico.roadside.stateMachine.AssistantTransaction;
import com.example.geico.roadside.stateMachine.AssistantTransactionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostGisRoadsideAssistanceServiceTest {

    @Mock
    AssistantRepository assistantRepository;

    @Mock
    AssistantTransactionRepository assistantTransactionRepository;

    @Mock
    PlatformTransactionManager platformTransactionManager;

    //class under test
    PostGisRoadsideAssistanceService service;
    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    @BeforeEach
    void setupTest(){
        service = new PostGisRoadsideAssistanceService(assistantRepository,assistantTransactionRepository,platformTransactionManager);
    }

    @Test
    void updateAssistantLocation() {
        //when
        String name = "bob";
        Point point = factory.createPoint(new Coordinate(0.0,0.0));
        AssistantModel model = new AssistantModel(UUID.randomUUID(),"bob",0.0,0.0,point);
        when(assistantRepository.findAssistantModelByName(name)).thenReturn(Optional.of(model));

        //then
        Geolocation location = new Geolocation(10.0,20.0);
        Assistant assistant = new Assistant(name);
        service.updateAssistantLocation(assistant,location);

        //verify
        verify(assistantRepository, times(1)).findAssistantModelByName("bob");
        verify(assistantRepository,times(1)).save(any(AssistantModel.class));
    }

    @Test
    void updateAssistantLocationAssistantNotFound() {
        //when
        when(assistantRepository.findAssistantModelByName("frank")).thenReturn(Optional.empty());

        //then
        Geolocation location = new Geolocation(10.0,20.0);
        Assistant assistant = new Assistant("frank");
        service.updateAssistantLocation(assistant,location);

        //verify
        verify(assistantRepository, times(1)).findAssistantModelByName("frank");
        verify(assistantRepository,times(0)).save(any(AssistantModel.class));
    }

    @Test
    void findNearestAssistants() {
        Geolocation geolocation = new Geolocation(33.9434, -118.4079);
        AssistantModel one = new AssistantModel(UUID.randomUUID(),"one",1.0,10.0,factory.createPoint(new Coordinate(1.0,10.0)));
        AssistantModel two = new AssistantModel(UUID.randomUUID(),"two",2.0,20.0,factory.createPoint(new Coordinate(2.0,20.0)));
        AssistantModel three = new AssistantModel(UUID.randomUUID(),"three",3.0,30.0,factory.createPoint(new Coordinate(3.0,30.0)));
        List<AssistantModel> closest = List.of(one,two,three);

        when(assistantRepository.findNearest(any(Point.class),eq(3))).thenReturn(closest);

        List<Assistant> assistantList = service.findNearestAssistants(geolocation,3);

        assertEquals(3,assistantList.size());
        Assistant oneA = assistantList.get(0);
        assertEquals("one",oneA.getName());
        assertEquals(1.0,oneA.getGeolocation().getLat());
        assertEquals(10.0,oneA.getGeolocation().getLon());

        //verify
        verify(assistantRepository,times(1)).findNearest(any(),anyInt());
    }

    @Test
    void reserveAssistant() {
        AssistantModel one = new AssistantModel(UUID.randomUUID(),"one",1.0,10.0,factory.createPoint(new Coordinate(1.0,10.0)));
        AssistantModel two = new AssistantModel(UUID.randomUUID(),"two",2.0,20.0,factory.createPoint(new Coordinate(2.0,20.0)));
        AssistantModel three = new AssistantModel(UUID.randomUUID(),"three",3.0,30.0,factory.createPoint(new Coordinate(3.0,30.0)));
        List<AssistantModel> closest = List.of(one,two,three);

        when(assistantRepository.findNearest(any(Point.class),eq(10))).thenReturn(closest);

        Customer me = new Customer(UUID.randomUUID(),"me");
        Geolocation geolocation = new Geolocation(33.9434, -118.4079);
        service.reserveAssistant(me,geolocation);

        verify(assistantTransactionRepository,atLeastOnce()).save(any());
    }

    @Test
    void releaseAssistant() {
        Customer customer = new Customer(UUID.fromString("60df08e8-81fc-4249-9b73-ea36ccd7e2ce"),"me");
        Assistant assistant = new Assistant(UUID.fromString("c84d4127-ab63-40cd-b09c-28adf6e64cf5"),null,null);

        AssistantTransaction transaction = new AssistantTransaction(null, UUID.fromString("60df08e8-81fc-4249-9b73-ea36ccd7e2ce"), AssistantState.ASSIGNED,true);
        when(assistantTransactionRepository.findAssistantTransactionByAssistantIdAndMostRecentTrue(UUID.fromString("c84d4127-ab63-40cd-b09c-28adf6e64cf5")))
                .thenReturn(Optional.of(transaction));

        service.releaseAssistant(customer,assistant);

        verify(assistantTransactionRepository,atLeastOnce()).save(any());
        verify(assistantTransactionRepository, times(2)).findAssistantTransactionByAssistantIdAndMostRecentTrue(any(UUID.class));
    }

    @Test
    void releaseAssistantNotFound() {
        Customer customer = new Customer(UUID.fromString("60df08e8-81fc-4249-9b73-ea36ccd7e2ce"),"me");
        Assistant assistant = new Assistant(UUID.fromString("c84d4127-ab63-40cd-b09c-28adf6e64cf5"),null,null);

        AssistantTransaction transaction = new AssistantTransaction(null, UUID.fromString("60df08e8-81fc-4249-9b73-ea36ccd7e2ce"), AssistantState.ASSIGNED,true);
        when(assistantTransactionRepository.findAssistantTransactionByAssistantIdAndMostRecentTrue(UUID.fromString("c84d4127-ab63-40cd-b09c-28adf6e64cf5")))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class,
                () ->{
                    service.releaseAssistant(customer,assistant);
                } );

        verify(assistantTransactionRepository,never()).save(any());
        verify(assistantTransactionRepository, times(1)).findAssistantTransactionByAssistantIdAndMostRecentTrue(any(UUID.class));
    }
}