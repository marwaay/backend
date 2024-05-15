package com.pfe.personnel.services;

import org.springframework.stereotype.Service;

@Service
public class JwtTokenLogoutService {
    private  final JwtTokenBlacklistService blacklistService;

    public JwtTokenLogoutService(JwtTokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    public void logout(String token) {
        blacklistService.addToBlacklist(token);
    }
}
