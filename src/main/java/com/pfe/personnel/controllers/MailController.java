package com.pfe.personnel.controllers;

import com.pfe.personnel.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
public class MailController {
    @Autowired
    private MailService mailService;


/*
    @PutMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam String email,
                                                @RequestParam String otp) {
        return new ResponseEntity<>(mailService.verifyAccount(email, otp), HttpStatus.OK);
    }
    @PutMapping("/regenerate-otp")
    public ResponseEntity<String> regenerateOtp(@RequestParam String email) {
        return new ResponseEntity<>(mailService.regenerateOtp(email), HttpStatus.OK);
    }
*/
    @PutMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email){
        return new ResponseEntity<String>(String.valueOf(mailService.forgotPassword(email)), HttpStatus.OK);
    }

    @PutMapping("/set-password")
    public ResponseEntity<String> setPassword(@RequestParam String email,
                                              @RequestParam String newPassword,
                                              @RequestParam String confirmPassword) {
        return new ResponseEntity<>(mailService.setPassword(email, newPassword, confirmPassword), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader String oldPassword,
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword){
      return new ResponseEntity<>( mailService.changePassword(email,oldPassword,newPassword,confirmPassword), HttpStatus.OK);
  }

















}
