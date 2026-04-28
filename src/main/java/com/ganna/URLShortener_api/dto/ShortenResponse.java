package com.ganna.URLShortener_api.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class ShortenResponse implements Serializable {
private static final long serialVersionUID = 1L;

    private String shortCode;
    private String shortUrl;
} 
