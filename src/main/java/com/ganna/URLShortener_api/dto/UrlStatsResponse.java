package com.ganna.URLShortener_api.dto;

import java.time.LocalDate;

public record UrlStatsResponse(String shortCode, String originalUrl, long totalClicks, String createdAt, LocalDate lastAccessedAt) {
}
