package com.example.mosaica.controllers;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.example.mosaica.MosaicaApplication.IMAGES_DIRECTORY;


@RestController
public class ImageController {

    @RequestMapping("/image/show/{path}")
    public ResponseEntity<?> getImage(@PathVariable("path") String path) {
        try {
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(new InputStreamResource(new FileInputStream(new File(IMAGES_DIRECTORY, path))), headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
