package com.faizly.urlshortener.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UrlShortenRequest {

    private String originalUrl;
    private String category;
    private String password;
    private LocalDateTime expiryDate;
}
