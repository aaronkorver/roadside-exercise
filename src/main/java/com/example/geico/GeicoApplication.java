package com.example.geico;

import com.example.geico.roadside.cli.RoadsideCli;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.command.annotation.EnableCommand;

@SpringBootApplication
@EnableCommand(RoadsideCli.class)
@CommandScan
public class GeicoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeicoApplication.class, args);
    }

}
