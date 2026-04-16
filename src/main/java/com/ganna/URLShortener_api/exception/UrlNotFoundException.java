package com.ganna.URLShortener_api.exception;

public class UrlNotFoundException extends RuntimeException {
    
    /**
     * @param shortCode the short code of the URL that was not found
     */
    public UrlNotFoundException(String shortCode) {
        super("URL not found for short code: " + shortCode);
    }
    
}
