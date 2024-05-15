package com.pfe.personnel.controllers;

import com.pfe.personnel.auth.AuthenticationRequest;
import com.pfe.personnel.auth.AuthenticationResponse;
import com.pfe.personnel.auth.RegisterRequest;
import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.repository.PersonnelRepo;
import com.pfe.personnel.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
   private final PersonnelRepo personnelRepo;

   @PostMapping(value = "login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request)
    {
        return ResponseEntity.ok(authService.login(request));


    }



    @PostMapping(value = "register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request)
    {
        System.out.println(request.getLogin());
        System.out.println(request.getMatricule());
        return ResponseEntity.ok(authService.register(request));

    }



    @Autowired
    private AuthenticationService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String login = authentication.getName();
        Optional<Personnel> userOptional = userService.getUserByUsername(login);

        if (userOptional.isPresent()) {
            Personnel user = userOptional.get();
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }





}

