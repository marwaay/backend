package com.pfe.personnel.controllers;

import com.pfe.personnel.models.Conge;
import com.pfe.personnel.models.Notification;
import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.models.dto.CountType;
import com.pfe.personnel.repository.CongeRepo;
import com.pfe.personnel.repository.NotifRepo;
import com.pfe.personnel.services.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class CongeController {
    @Autowired
    private CongeService congeService;
    @Autowired
    private NotifService notifService;
    @Autowired
    private MailService emailService;

    @Autowired
    private NotifRepo notifRepo;

    @Autowired
    private EmpService empService;
@Autowired
    private CongeRepo leaveRequestRepository ;

@Autowired
CongeTriggerService congeTriggerService;
    @PostMapping("/refuserconge/{congeId}")
    public ResponseEntity<Conge> refuserConge(@PathVariable Long congeId) {
        Conge conge = congeService.refuseConge((long) Math.toIntExact(congeId));

        Personnel user = congeService.getuser(congeId);
        String recipient = user.getEmail();
        String sender = user.getEmail();
        String subject = "Validation congé";
        String body = "Votre demande de congé a été Refusé,";
        Notification notification = new Notification();

        notification.setSubject(subject);
        notification.setContent( body + "Cette demande est de type " + conge.getType() + " du " + conge.getDate_debut() + " au " + conge.getDate_fin() + ". Demande faite le " + conge.getDate_demande());
        notification.setSender_id(conge.getChefId());
        notification.setRecipient_id(user.getId());
        notification.setLu(0);
        notification.setSent_at(Timestamp.from(Instant.now()));

        notifRepo.save(notification);

        try {
            emailService.sendEmail(sender, recipient, subject, body);

            return ResponseEntity.ok(conge);
        } catch (MessagingException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    private static final String UPLOAD_DIR = "monapp/src/main/resources/static/";

    @PostMapping("/confirmerconge/{congeId}")
    public ResponseEntity<Conge> confirmerConge(@PathVariable Long congeId) throws MessagingException {
        Conge conge = congeService.confirmerConge((long) Math.toIntExact(congeId));
        Personnel  user = congeService.getuser(congeId);
        String recipient = user.getEmail();
        String sender = user.getEmail();
        String subject = "Validation congé";
        String body = "Votre demande de congé a été Confirmé,";
        Notification notification = new Notification();

        notification.setSubject(subject);
        notification.setContent(body + "Cette demande est de type " + conge.getType() + " du " + conge.getDate_debut() + " au " + conge.getDate_fin() + ". Demande faite le " + conge.getDate_demande());
        notification.setSender_id(conge.getChefId());
        notification.setRecipient_id(user.getId());
        notification.setLu(0);
        notification.setSent_at(Timestamp.from(Instant.now()));
        notifRepo.save(notification);


      //  conge.setChefId(empService.getChefIdByUserService(user.getId()));
        try {
            emailService.sendEmail(sender, recipient, subject, body);

            return ResponseEntity.ok(conge);
        } catch (MessagingException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/archive")
    public ResponseEntity<List<Conge>> getArchiveConges() {
        List<Conge> archiveConges = congeService.getArchive();
        return new ResponseEntity<>(archiveConges, HttpStatus.OK);
    }

    @GetMapping("/congeconfirme")
    public ResponseEntity<List<Conge>> getCongesConfirme() {
        List<Conge> archiveConges = congeService.getCongesConfirmes();
        return new ResponseEntity<>(archiveConges, HttpStatus.OK);
    }



@GetMapping("/conges")
    public List<Conge> getconges(){
        return congeService.getconges();
}

    @GetMapping("/percentCountType")
    public List<CountType> getPercentageGroupByType() {
        return congeService.getPercentageGroupByType();
    }

    @GetMapping("/percentCountType/{userId}")
    public List<CountType> getPercentageGroupByTypeForUser(@PathVariable Long userId) {
        return congeService.getPercentageGroupByTypeForUser(userId);
    }


    @GetMapping("/countByUser")
    public Map<Long, Long> countCongesByUser() {
        return congeService.countCongesByUser();
    }


    @GetMapping("/countByUser/{userId}")
    public Long countCongesByUser(@PathVariable Long userId) {
        return congeService.countCongesByUser(userId);
    }

    @GetMapping("/user/{userId}/confirmed/count-and-percentage")
    public List<Object[]> getCountAndPercentageOfConfirmedCongesByUser(@PathVariable Long userId) {
        return congeService.getCountAndPercentageOfConfirmedCongesByUser(userId);
    }



    @GetMapping("/count-by-statut/{userId}")
    public List<Object[]> countCongesByStatut(@PathVariable Long userId) {
        return congeService.countCongesByStatut(userId);
    }





}

