package com.pfe.personnel.models;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor
public enum ROLe {
    EMPLOYEE,
    ADMIN,
    CHEF;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.name()));
    }
}

