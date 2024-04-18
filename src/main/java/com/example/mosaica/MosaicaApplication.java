package com.example.mosaica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

@SpringBootApplication
@EnableJpaRepositories
public class MosaicaApplication {
    public static final String IMAGES_DIRECTORY = "images";
    public static final Random RANDOM_GENERATOR = new Random();

    static {
        try {
            Files.createDirectories(Path.of(IMAGES_DIRECTORY));
        } catch (IOException ignored) {
        }
    }

    public static void main(String... args) {
        SpringApplication.run(MosaicaApplication.class, args);
    }
}
