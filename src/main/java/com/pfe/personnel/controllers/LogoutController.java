package com.pfe.personnel.controllers;

import com.pfe.personnel.services.JwtTokenLogoutService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public class LogoutController {
    private final JwtTokenLogoutService logoutService;

    public LogoutController(JwtTokenLogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @PostMapping("/logout")
    public HttpStatus logout(@RequestHeader("Authorization") String token) {
        logoutService.logout(token);
        return HttpStatus.OK;
    }
}
