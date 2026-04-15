package com.ganna.URLShortener_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ganna.URLShortener_api.service.UrlService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/urls")
public class UrlController {
    
    private final UrlService urlService;

    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String( );
    }
    
    @PostMapping("path")
    public String postMethodName(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }

    @DeleteMapping("path")
    public void deleteMethodName(@RequestParam String param) {
    }
    
    

}
