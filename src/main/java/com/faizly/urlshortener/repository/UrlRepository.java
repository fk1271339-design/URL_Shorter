package com.faizly.urlshortener.repository;

import com.faizly.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);

    List<Url> findByFavouriteTrue();

    List<Url> findByCategoryIgnoreCase(String category);

    boolean existsByShortCode(String shortCode);

    List<Url> findByOriginalUrlContainingIgnoreCase(String keyword);
}
