package com.ganna.URLShortener_api.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.ganna.URLShortener_api.config.CacheConfig;
import com.ganna.URLShortener_api.dto.ShortenRequest;
import com.ganna.URLShortener_api.dto.ShortenResponse;
import com.ganna.URLShortener_api.dto.UrlStatsResponse;
import com.ganna.URLShortener_api.model.ShortUrl;
import com.ganna.URLShortener_api.repository.UrlRepository;
import com.ganna.URLShortener_api.exception.UrlNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    // Shorten url logic

    /**
     * Shortens the given URL.
     *
     * @param request the original URL to be shortened
     * @param baseUrl the server base URL for the shortened link
     * @return the shortened URL
     */

    @Transactional
    public ShortenResponse shortenUrl(ShortenRequest request, String baseUrl) {
        String shortCode = generateUniqueShortCode();

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(request.originalUrl())
                .shortCode(shortCode)
                .clickCount(0L)
                .build();

        urlRepository.save(shortUrl);

        log.info("URL shortened: {} -> {}", request.originalUrl(), shortCode);

        return new ShortenResponse(shortCode, baseUrl + "/" + shortCode);

    }

    // redirect logic

    /**
     * Returns the original URL corresponding to the given short code.
     * 
     * @param shortCode the short code to look up
     * @return the original URL
     * @throws UrlNotFoundException if the short code is not found
     */

    @Cacheable(value = CacheConfig.CACHE_URLS, key = "#shortCode") //Check cache first before hitting the database
    @CacheEvict(value = CacheConfig.CACHE_STATS, key = "#shortCode") //Evict stats cache entry for the short code when accessed
    @Transactional
    public String getOriginalUrl(String shortCode) {

        log.debug("Cache-MISS urls - loading from DB: {}", shortCode);

        ShortUrl shortUrl = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Short code not found: {}", shortCode);
                    throw new UrlNotFoundException(shortCode);
                });

        urlRepository.incrementClickCountAndSetLastAccessedAt(shortCode);

        log.info("Redirect: {} -> {}", shortCode, shortUrl.getOriginalUrl());

        return shortUrl.getOriginalUrl();

    }

    // delete logic

    /**
     * Deletes the URL corresponding to the given short code.
     *
     * @param shortCode the short code of the URL to delete
     * @throws UrlNotFoundException if the short code is not found
     */

    @Transactional
    @Caching(evict = { //Evict cache entries for the short code when a URL is deleted
        @CacheEvict(value = CacheConfig.CACHE_URLS, key = "#shortCode"),
        @CacheEvict(value = CacheConfig.CACHE_STATS, key = "#shortCode")
    })
    public void deleteUrl(String shortCode) {

        ShortUrl shortUrl = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Short code not found for deletion: {}", shortCode);
                    throw new UrlNotFoundException(shortCode);
                });

        urlRepository.delete(shortUrl);

        log.info("URL deleted: {}", shortCode);

    }

    // Stats logic

    /**
     * Returns statistics for the given short code.
     *
     * @param shortCode the short code for which to retrieve statistics
     * @return UrlStatsResponse containing the statistics
     * @throws UrlNotFoundException if the short code is not found
     */

     @Cacheable(value = CacheConfig.CACHE_STATS, key = "#shortCode") //Check cache first before hitting the database
    public UrlStatsResponse getStats(String shortCode) {

        log.debug("Cache-MISS stats - loading from DB: {}", shortCode);

        ShortUrl shortUrl = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Short code not found for stats: {}", shortCode);
                    throw new UrlNotFoundException(shortCode);
                });

        log.info("GET /api/stats/{} - Clicks: {}, CreatedAt: {}, LastAccessedAt: {}",
                shortCode, shortUrl.getClickCount(), shortUrl.getCreatedAt(), shortUrl.getLastAccessedAt());

        return new UrlStatsResponse(
                shortUrl.getShortCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt(),
                shortUrl.getLastAccessedAt()
        );
    }

    // All URLs logic

     /**
     * Returns statistics for all shortened URLs.
     *
     * @return a list of UrlStatsResponse containing the statistics for all URLs
     */
   
    public List<UrlStatsResponse> getAllStats() {
        return urlRepository.findAll().stream()
                .map(shortUrl -> new UrlStatsResponse(
                        shortUrl.getShortCode(),
                        shortUrl.getOriginalUrl(),
                        shortUrl.getClickCount(),
                        shortUrl.getCreatedAt(),
                        shortUrl.getLastAccessedAt()
                ))
                .collect(Collectors.toList());
            }

    // Helper method to generate a unique short code

    private String generateUniqueShortCode() {
        String shortCode;

        do {
            shortCode = generateShortCode();
        } while (urlRepository.existsByShortCode(shortCode));

        return shortCode;
    }

    // Helper method to generate a random short code

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);

        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        
        return sb.toString();
    }
}
