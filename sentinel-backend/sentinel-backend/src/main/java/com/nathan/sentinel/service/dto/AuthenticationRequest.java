package com.nathan.sentinel.service.dto;

public record AuthenticationRequest (
    String username,
    String password
) {
    
}
