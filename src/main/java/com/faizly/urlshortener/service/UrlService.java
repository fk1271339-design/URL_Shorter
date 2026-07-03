package com.faizly.urlshortener.service;

import com.faizly.urlshortener.dto.UrlShortenRequest;
import com.faizly.urlshortener.dto.UrlShortenResponse;
import com.faizly.urlshortener.entity.Url;
import com.faizly.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.faizly.urlshortener.dto.UrlResponse;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlShortenResponse shortenUrl(UrlShortenRequest request) {
        String shortCode = generateUniqueShortCode();

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .clickCount(0)
                .active(true)
                .favourite(false)
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);

        String shortUrl = "http://localhost:8080/" + shortCode;

        return new UrlShortenResponse(shortUrl);
    }
    public List<UrlResponse> getAllUrls() {
        return urlRepository.findAll()
                .stream()
                .map(this::mapToUrlResponse)
                .toList();
    }

    public void deleteUrl(Long id) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        urlRepository.delete(url);
    }

    public UrlResponse toggleUrlStatus(Long id) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        url.setActive(!url.isActive());
        Url savedUrl = urlRepository.save(url);

        return mapToUrlResponse(savedUrl);
    }

    private UrlResponse mapToUrlResponse(Url url) {
        return new UrlResponse(
                url.getId(),
                url.getOriginalUrl(),
                "http://localhost:8080/" + url.getShortCode(),
                url.getShortCode(),
                url.getClickCount(),
                url.isActive(),
                url.isFavourite(),
                url.getCreatedAt()
        );

    }
    public UrlResponse updateUrl(Long id, UrlShortenRequest request) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        url.setOriginalUrl(request.getOriginalUrl());

        Url savedUrl = urlRepository.save(url);

        return mapToUrlResponse(savedUrl);
    }
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        if (!url.isActive()) {
            throw new RuntimeException("This link is disabled");
        }

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        return url.getOriginalUrl();
    }
    public List<UrlResponse> searchUrls(String keyword) {
        return urlRepository.findByOriginalUrlContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToUrlResponse)
                .toList();
    }
    private String generateUniqueShortCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        String shortCode;

        do {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < 6; i++) {
                int index = random.nextInt(characters.length());
                builder.append(characters.charAt(index));
            }

            shortCode = builder.toString();
        } while (urlRepository.existsByShortCode(shortCode));

        return shortCode;
    }
    public UrlResponse toggleFavourite(Long id) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        url.setFavourite(!url.isFavourite());

        Url savedUrl = urlRepository.save(url);

        return mapToUrlResponse(savedUrl);
    }

    public List<UrlResponse> getFavouriteUrls() {
        return urlRepository.findByFavouriteTrue()
                .stream()
                .map(this::mapToUrlResponse)
                .toList();
    }
}