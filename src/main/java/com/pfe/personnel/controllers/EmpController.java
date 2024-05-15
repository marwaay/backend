package com.pfe.personnel.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.personnel.models.Conge;
import com.pfe.personnel.models.Notification;
import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.models.TypeConge;
import com.pfe.personnel.repository.CongeRepo;
import com.pfe.personnel.services.*;
import com.pfe.personnel.util.FileUploadUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/conge")
public class EmpController {
    private final EmpService employeService;
    private final NotifService notService;


    @Autowired
    private PersonnelService personnelService;

    @Autowired
    private CongeTriggerService congeTriggerService;
    @Autowired
    private CongeRepo congeRepo;
    @Autowired
    private MailService mailService;
    @Autowired
    private EmpService empService;

    public EmpController(EmpService employeService, NotifService notService, MailService emailService, CongeTriggerService congeTriggerService, CongeRepo congeRepo) {
        this.employeService = employeService;
        this.notService = notService;


        this.congeRepo = congeRepo;
    }


    @GetMapping("/afficherconges")
    public List<Conge> affichertousConges() {
        return employeService.afficherconges();
    }

    @GetMapping("/affichernotification")
    public List<Notification> affichernotifications() {
        return notService.affichernotifications();
    }

    public static String uploadDirectory = System.getProperty("user.id") + "src/main/webapp/images";
/*
    @PostMapping("/ajouterconge")
    public ResponseEntity<?> ajouterConge(@RequestBody Conge conge) {
        try {
            // Check if someone from the same service has already taken leave
            if (isLeaveTakenBySameService(conge)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Someone from your service has already taken leave.");
            }

            // Call the service method to add the leave request
            Conge savedConge = congeRepo.save(conge);
            return ResponseEntity.ok(savedConge);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

*/
    private boolean isLeaveTakenBySameService(Conge conge) {
        // Fetch the personnel object by user ID
        Optional<Personnel> personnelOptional = personnelService.findById(conge.getUser().getId());
        if (personnelOptional.isEmpty()) {
            // Personnel not found
            return false;
        }


        String requestingService = personnelOptional.get().getService();


        long overlappingLeaveCount = congeRepo.countOverlappingLeaveRequests(requestingService, conge.getDate_debut(), conge.getDate_fin(), "Confirmé");


        if (overlappingLeaveCount >= 2) {
            return true; // Block the leave request if there are already two
        }


        return false;
    }


@PostMapping("/ajouterconge")
public ResponseEntity<Conge> ajouterConge(@RequestParam("file") MultipartFile file, @RequestParam("conge") String conge) throws MessagingException {
    try {

        Conge conges = new ObjectMapper().readValue(conge, Conge.class);

        String fileName = null;
        if (file != null) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            conges.setFile(fileName);
        }
        // Save user to database
        conges.setDate_demande(new Date());

        // Check if someone from the same service has already taken leave
        if (isLeaveTakenBySameService(conges)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Add the block of code here
        Optional<Personnel> personnelOptional = personnelService.findById(conges.getUser().getId());
        if (personnelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Personnel personnel = personnelOptional.get();

        // Calculater maternite conge
        int dureeConge = 0;
        if (conges.getType() == TypeConge.MATERNITE) {
            dureeConge = calculerDureeCongeMaternite(personnel);

            // Set maternite conge
            conges.setDuree(dureeConge);

            // date fin (calcul)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(conges.getDate_debut());
            calendar.add(Calendar.DAY_OF_MONTH, dureeConge); // Add duration to start date
            Date dateFin = calendar.getTime();
            conges.setDate_fin(dateFin);
        } else {

            int duree = conges.getDuree();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(conges.getDate_debut());
            calendar.add(Calendar.DAY_OF_MONTH, duree);
            Date dateFin = calendar.getTime();
            conges.setDate_fin(dateFin);
        }
        conges.setChefId(empService.getChefIdByUserService(conges.getUser().getId()));
        Conge savedConge = congeRepo.save(conges);

        // Save image file if multipartFile is not null
        if (file != null) {
            String uploadDir = "user-photos/";
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }

        congeTriggerService.createCongeTrigger();

//congeTriggerService.createCRcongeTrigger();

        return ResponseEntity.ok(savedConge);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(null);
    }
}





    @GetMapping("/images/{congeId}/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long congeId, @PathVariable String fileName) throws IOException {

        Optional<Conge> congeOptional = congeRepo.findById(congeId);
        if (congeOptional.isEmpty()) {
            String errorMessage = "Conge not found with ID: " + congeId;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.getBytes());
        }

        // Construct the path to the file
        String filePath = "user-photos/" + fileName;
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            String errorMessage = "File not found for conge ID: " + congeId + " and file name: " + fileName;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.getBytes());
        }

        // Read the file as bytes
        byte[] fileData = Files.readAllBytes(path);

        // Determine the content type based on file extension
        String contentType = determineContentType(fileName);

        // Set content type header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));

        // Serve the file data as a response
        return ResponseEntity.ok().headers(headers).body(fileData);
    }


    private String determineContentType(String fileName) {
        String[] parts = fileName.split("\\.");
        String extension = parts[parts.length - 1].toLowerCase();
        switch (extension) {
            case "pdf":
                return MediaType.APPLICATION_PDF_VALUE;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG_VALUE;
            case "png":
                return MediaType.IMAGE_PNG_VALUE;
            // Add more cases for other file types as needed
            default:
                return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }




    @GetMapping("/current-user-id")
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                Personnel userDetails = (Personnel) principal;
                return userDetails.getId();
            }
        }
        return null;
    }


    @GetMapping("/get-nbr-enfant")
    public ResponseEntity<Integer> getNbrEnfant(@RequestParam Long userId) {
        try {
            // Fetch the Personnel object by user ID
            Optional<Personnel> personnelOptional = personnelService.findById(userId);
            if (personnelOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Get the Personnel object
            Personnel personnel = personnelOptional.get();

            // Get and return the number of children
            int nbrEnfant = personnel.getNbrEnfant();

            // Calculate maternity leave duration
            int dureeCongeMaternite = calculerDureeCongeMaternite(personnel);

            return ResponseEntity.ok(dureeCongeMaternite);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public int calculerDureeCongeMaternite(Personnel personnel) {
        int nombreEnfants = personnel.getNbrEnfant();
        int dureeCongeMaternite = 0;

        // Calculate maternity leave duration based on the number of children
        if (nombreEnfants == 1 || nombreEnfants == 2) {
            dureeCongeMaternite = 16 * 7; // 16 weeks for 1st and 2nd child
        } else if (nombreEnfants >= 3) {
            dureeCongeMaternite = 26 * 7; // 26 weeks for 3rd child and more
        }

        return dureeCongeMaternite;
    }

/*
    @Autowired
    EmpService empService;
    @PostMapping("/ajouter")
    public ResponseEntity<Conge> ajouterConge(@RequestBody Conge conge) {
        try {
            // Fetch the Personnel object by user ID
            Optional<Personnel> personnelOptional = personnelService.findById(conge.getUser().getId());
            if (personnelOptional.isEmpty()) {
                // Handle personnel not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Get the Personnel object
            Personnel personnel = personnelOptional.get();

            // Calculate maternity leave duration if the type of leave is "MATERNITE"
            int dureeConge = 0;
            if (conge.getType() == TypeConge.MATERNITE) {
                dureeConge = calculerDureeCongeMaternite(personnel);
            }

            // Set maternity leave duration
            conge.setDuree(dureeConge);

            // Save the Conge object
            Conge savedConge = congeRepo.save(conge);
            return ResponseEntity.ok(savedConge);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
*/
}
