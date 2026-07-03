package com.faizly.urlshortener.controller;

import com.faizly.urlshortener.dto.UrlResponse;
import java.util.List;
import com.faizly.urlshortener.dto.UrlShortenRequest;
import com.faizly.urlshortener.dto.UrlShortenResponse;
import com.faizly.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public UrlShortenResponse shortenUrl(@RequestBody UrlShortenRequest request) {
        return urlService.shortenUrl(request);
    }

    @GetMapping("/all")
    public List<UrlResponse> getAllUrls() {
        return urlService.getAllUrls();
    }

    @DeleteMapping("/{id}")
    public String deleteUrl(@PathVariable Long id) {
        urlService.deleteUrl(id);
        return "URL deleted successfully";
    }

    @PatchMapping("/{id}/toggle-status")
    public UrlResponse toggleUrlStatus(@PathVariable Long id) {
        return urlService.toggleUrlStatus(id);
    }

    @PutMapping("/{id}")
    public UrlResponse updateUrl(@PathVariable Long id, @RequestBody UrlShortenRequest request) {
        return urlService.updateUrl(id, request);
    }
    @GetMapping("/search")
    public List<UrlResponse> searchUrls(@RequestParam String keyword) {
        return urlService.searchUrls(keyword);
    }
    @PatchMapping("/{id}/favourite")
    public UrlResponse toggleFavourite(@PathVariable Long id) {
        return urlService.toggleFavourite(id);
    }

    @GetMapping("/favourites")
    public List<UrlResponse> getFavouriteUrls() {
        return urlService.getFavouriteUrls();
    }
}