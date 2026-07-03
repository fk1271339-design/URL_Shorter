package com.faizly.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UrlResponse {

    private Long id;
    private String originalUrl;
    private String shortUrl;
    private String shortCode;
    private int clickCount;
    private boolean active;
    private boolean  favourite;
    private LocalDateTime createdAt;
}