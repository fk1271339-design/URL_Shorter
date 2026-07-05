package com.faizly.urlshortener.controller;

import com.faizly.urlshortener.dto.PasswordRequest;
import com.faizly.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{shortCode}/password")
    public ResponseEntity<Void> redirectWithPassword(
            @PathVariable String shortCode,
            @RequestBody PasswordRequest request
    ) {
        String originalUrl = urlService.getOriginalUrlWithPassword(shortCode, request.getPassword());

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}
