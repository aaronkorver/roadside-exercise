package com.example.geico.roadside.cli;

import com.example.geico.roadside.Assistant;
import com.example.geico.roadside.Customer;
import com.example.geico.roadside.Geolocation;
import com.example.geico.roadside.RoadsideAssistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.UUID;


@Command
public class RoadsideCli {

    RoadsideAssistanceService service;

    public RoadsideCli(RoadsideAssistanceService service) {
        this.service = service;
    }

    @Command(command = "update",description = "Updates an assistant's position")
    public void updateAssistant(@Option(required = true) String name, String latlon) {
        String[] splitarray = latlon.split(",");
        Geolocation location = new Geolocation(Double.parseDouble(splitarray[0]),Double.parseDouble(splitarray[1]));
        Assistant assistant = new Assistant(name);
        service.updateAssistantLocation(assistant, location);
    }
    @Command(command = "nearest", description = "find nearest N locations to the users position")
    public void findNearest(int limit){
        Geolocation geolocation = new Geolocation(33.9434, -118.4079); //hard coding position to LAX airport for demo
        List<Assistant> assistantList = service.findNearestAssistants(geolocation,limit);
        System.out.println(assistantList);
    }
    @Command(command = "reserve", description = "reserve an assistant for a customer")
    public void reserve(){
        Customer me = new Customer(UUID.randomUUID(),"me");
        Geolocation geolocation = new Geolocation(33.9434, -118.4079);
        service.reserveAssistant(me,geolocation);
    }
    @Command(command = "release", description = "release a customer from an assistant")
    public void release(@Option(required = true) String custId, @Option(required = true) String assistId){
        Customer customer = new Customer(UUID.fromString(custId),"me");
        Assistant assistant = new Assistant(UUID.fromString(assistId),null,null);
        service.releaseAssistant(customer,assistant);
    }
}
