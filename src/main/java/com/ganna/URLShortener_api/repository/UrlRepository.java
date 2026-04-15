package com.ganna.URLShortener_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ganna.URLShortener_api.model.ShortUrl;

public interface UrlRepository extends JpaRepository<ShortUrl, Long> {
   Optional<ShortUrl> findByShortUrl(String shortUrl);

   boolean existsByShortCode(String shortCode);

   @Modifying
   @Query("UPDATE ShortUrl u SET u.clickCount = u.clickCount + 1, u.lastAccessedAt = CURRENT_TIMESTAMP WHERE u.shortCode = :shortCode")
   void incrementClickCountAndSetLastAccessedAt(@Param("shortCode") String shortCode);
}

