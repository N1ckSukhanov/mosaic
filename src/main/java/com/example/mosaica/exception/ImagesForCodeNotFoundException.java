package com.example.mosaica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Images for this code haven't been generated.")
public class ImagesForCodeNotFoundException extends RuntimeException {
}
