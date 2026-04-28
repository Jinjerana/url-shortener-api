package com.ganna.URLShortener_api.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ganna.URLShortener_api.dto.ShortenRequest;
import com.ganna.URLShortener_api.dto.ShortenResponse;
import com.ganna.URLShortener_api.dto.UrlStatsResponse;
import com.ganna.URLShortener_api.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UrlController {
    
    private final UrlService urlService;

    //shorten url logic

    /**
    * @param request the original URL to be shortened
    * @param httpRequest tu extract base URL dynamically
    * @return the shortened URL 201 Created
    */

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shortenUrl(
            @RequestBody ShortenRequest request,
            HttpServletRequest httpRequest) {

        validateUrl(request.originalUrl());

        // Dynamically construct the base URL from the incoming request
        String baseUrl = httpRequest.getScheme() + "://" + httpRequest.getServerName() + ":" + httpRequest.getServerPort();
        ShortenResponse response = urlService.shortenUrl(request, baseUrl);
        log.info("POST /api/shorten -> {}", response.getShortCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // redirect logic

    /**
     * @param shortCode the short code to look up
     * @return a 302 Found response with the Location header set to the original URL
     */

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        
        String originalUrl = urlService.getOriginalUrl(shortCode);
        
        log.info("GET /{} -> redirecting to {}", shortCode, originalUrl);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, originalUrl);

        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

     // delete logic

     /**
      * Deletes the shortened URL corresponding to the given short code.
      *
      * @param shortCode the short code of the URL to delete
      * @return a 204 No Content response if deletion is successful
      */

     @DeleteMapping("/api/urls/{shortCode}")
     public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
         
        urlService.deleteUrl(shortCode);

        log.info("DELETE /api/urls/{} -> deleted", shortCode);
        
        return ResponseEntity.noContent().build();
     }

     // stats logic

     /**
      * Retrieves statistics for the given short code.
      * @param shortCode the short code for which to retrieve statistics
      * @return a 200 OK response with the statistics in the body
    */
        @GetMapping("/api/stats/{shortCode}")
        public ResponseEntity<UrlStatsResponse> getStats(@PathVariable String shortCode) {
            
            UrlStatsResponse stats = urlService.getStats(shortCode);
    
            log.info("GET /api/stats/{} -> clicks: {}, createdAt: {}, lastAccessedAt: {}",
                    shortCode, stats.getTotalClicks(), stats.getCreatedAt(), stats.getLastAccessedAt());
            
            return ResponseEntity.ok(stats);
        }

        /**
         * all statistics for the given short code.
         * @return a 200 OK response with the statistics in the body
         */
    
        @GetMapping("/api/stats")
        public ResponseEntity<List<UrlStatsResponse>> getAllStats() {
            
            List<UrlStatsResponse> stats = urlService.getAllStats();
    
            log.info("GET /api/stats -> total URLs: {}", stats.size());
            
            return ResponseEntity.ok(stats);
        }
        // helper method to validate URL format
    
        /**
         * Validates the format of a URL.
         *
         * @throws IllegalArgumentException if the URL format is invalid
         */

        private void validateUrl(String url) {
            if(url == null || url.isBlank()) {
                throw new IllegalArgumentException("URL cannot be null or blank");
            }
            if(!url.startsWith("http://") && !url.startsWith("https://")) {
                throw new IllegalArgumentException("URL must start with http:// or https://");
            }
        }    
}
