package com.chatapp.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MediaConfig implements WebMvcConfigurer {

    @Value("${app.media.path}")
    private String mediaPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded images at /media/**
        String location = "file:" + System.getProperty("user.dir") + "/" + mediaPath + "/";
        registry.addResourceHandler("/media/**")
                .addResourceLocations(location);
    }
}
