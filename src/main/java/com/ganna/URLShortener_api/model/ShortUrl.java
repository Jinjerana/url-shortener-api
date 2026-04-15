package com.ganna.URLShortener_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "short_urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUrl implements Serializable {

    /** for Redis serialization*/
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    /** The short code for the URL */
    @Column(name = "short_code", nullable = false, unique = true, length = 10)
    private String shortCode;

    /** The original long URL */
    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl; 

    /** The number of redirects, default 0 */
    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    /** The creation timestamp, set once */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** The last accessed timestamp, updated on each redirect */
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    /** Automatically set timestamps */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /** Update last accessed time on each update */
    @PreUpdate
    protected void onUpdate() {
        this.lastAccessedAt = LocalDateTime.now();
    }

}
