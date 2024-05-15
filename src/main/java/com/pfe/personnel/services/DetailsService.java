package com.pfe.personnel.services;

import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.repository.PersonnelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DetailsService implements UserDetailsService {
    @Autowired
    private PersonnelRepo personnelRepo;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Personnel> userOptional = personnelRepo.findByLogin(login);
        Personnel personnel = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));
        return personnel;
    }}