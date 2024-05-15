package com.pfe.personnel.controllers;

import com.pfe.personnel.models.Notification;
import com.pfe.personnel.repository.NotifRepo;
import com.pfe.personnel.services.NotifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
public class NotificationController {
    @Autowired
    private NotifService notService;
    @Autowired
    private NotifRepo notRepo;

    @DeleteMapping("/supprimernotification/{id}")
    public void supprimerchef(@PathVariable Long id) {
        notService.supprimernotification(id);
    }



    @GetMapping("/notification/{id}")
    public Optional<Notification> affichernotification(@PathVariable Long id) {

        return notService.affichernotification(id);
    }

    @PutMapping("/notification/{notificationId}")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long notificationId) {
        Notification notification = notRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        notification.setLu(1);

        notRepo.save(notification);

        return ResponseEntity.status(HttpStatus.OK).body("Notification marked as read successfully");
    }

    @GetMapping("/countLu")
    public ResponseEntity<Long> countNotificationsLu() {
        long count = notService.countNotificationsLu();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }



    @GetMapping("/affichernotification")
    public List<Notification> affichernotifications() {return notService.affichernotifications();}

}