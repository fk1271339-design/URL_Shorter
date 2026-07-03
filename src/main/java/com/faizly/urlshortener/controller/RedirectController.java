package com.faizly.urlshortener.controller;

import com.faizly.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}