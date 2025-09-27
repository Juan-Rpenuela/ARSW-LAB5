package edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "edu.eci.arsw.blueprints")
public class BlueprintsMain {

    public static void main(String[] args) {
        SpringApplication.run(BlueprintsMain.class, args);
    }
}
