package com.skybooking.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal Jwt jwt) {
        String givenName = jwt.getClaim("given_name");
        String lastName = jwt.getClaim("family_name");
        return "Hello, " + givenName + " " + lastName + "!";
    }
}
