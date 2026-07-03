package com.faizly.urlshortener.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlShortenRequest {

    private String originalUrl;
}