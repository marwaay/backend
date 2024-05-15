package com.pfe.personnel.services;

import com.pfe.personnel.auth.AuthenticationResponse;
import com.pfe.personnel.auth.RegisterRequest;
import com.pfe.personnel.models.Conge;
import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.models.ROLe;
import com.pfe.personnel.models.TypeConge;
import com.pfe.personnel.models.dto.CountRole;
import com.pfe.personnel.models.dto.CountSexe;
import com.pfe.personnel.password.ChangePasswordRequest;
import com.pfe.personnel.repository.CongeRepo;
import com.pfe.personnel.repository.PersonnelRepo;
import com.pfe.personnel.sms.SmsRequest;
import jakarta.transaction.Transactional;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class PersonnelService  {

    private final JwtService jwtService;

    private final PersonnelRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired

    private PersonnelRepo personnelRepo;

    private final SmsService smsService;


    public PersonnelService(AuthenticationService authService, JwtService jwtService, PersonnelRepo userRepository, PasswordEncoder passwordEncoder, PersonnelRepo personnelRepo, SmsService smsService, PersonnelRepo repository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.personnelRepo = personnelRepo;
        this.smsService = smsService;
        this.repository = repository;
    }


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }
        return password.toString();
    }


    public List<Personnel> afficher() {
        return personnelRepo.findAll();
    }

    public Personnel getPersonnelById(Long id) {
        return personnelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel does not exist with id: " + id));
    }



    public  List<Personnel> getPersonnelByRole(String role) {
        ROLe roleEnum = ROLe.valueOf(role);
        if (roleEnum == null) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        return personnelRepo.findByRole(roleEnum);
    }




    //*public Personnel ajouterPersonnel(Personnel personnel) {
     //* return personnelRepo.save(personnel);}
    public AuthenticationResponse ajouterPersonnel(RegisterRequest request) {
        Long id = request.getId() != null ? Long.valueOf(request.getId()) : null;
        ROLe role;

        // Générer le mot de passe une fois
        String generatedPassword = generatePassword(10);

        switch (request.getRole()) {
            case "ADMIN":
                role = ROLe.ADMIN;
                break;
            case "EMPLOYEE":
                role = ROLe.EMPLOYEE;
                break;
            case "CHEF":
                role = ROLe.CHEF;
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        // Construire l'utilisateur avec le mot de passe généré
        Personnel user = Personnel.builder()
                .password(passwordEncoder.encode(generatedPassword)) // Utiliser le même mot de passe généré
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .service(request.getService())
                .cin(request.getCin())
                .tel(request.getTel())
                .sexe(request.getSexe())
                .statut(request.getStatut())
                .nbrEnfant(request.getNbrEnfant())
                .sexe(request.getSexe())
                .email(request.getEmail())
                .solde(60)
                .id(id)
                .role(role)
                .build();
        user.setMatricule(generateMatricule());
        user.setLogin(user.getMatricule());


        // Enregistrer l'utilisateur
        userRepository.save(user);

        // Envoyer l'e-mail avec le même mot de passe généré
        sendWelcomeEmailAndSms(user.getEmail(), user.getMatricule(), generatedPassword, request.getNom(), user.getTel());

        // Retourner la réponse d'authentification avec le token JWT
        return AuthenticationResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }
    @Autowired
    private JavaMailSender javaMailSender;
    // Méthode pour envoyer l'e-mail et le SMS
    private void sendWelcomeEmailAndSms(String toEmail, String username, String password, String matricule, String phoneNumber) {
        // Construire le message pour l'e-mail
        SimpleMailMessage messageEmail = new SimpleMailMessage();
        messageEmail.setTo(toEmail);
        messageEmail.setSubject("Bienvenue dans Notre Entreprise");
        messageEmail.setText("Cher " + matricule + ",\n\n"
                + "Bienvenue dans notre entreprise ! Vos identifiants de connexion sont les suivants :\n\n"
                + "Nom d'utilisateur : " + username + "\n"
                + "Mot de passe : " + password + "\n\n"
                + "Veuillez garder vos identifiants de connexion en sécurité.\n\n"
                + "Cordialement,\n"
                + "Leave Soft");

        // Envoyer l'e-mail
        javaMailSender.send(messageEmail);

        // Construire le message pour le SMS
        String messageSms = "Bonjour " + matricule + ",\n" +
                "Bienvenue sur Leave Soft ! Votre compte a été créé avec succès.\n" +
                "Voici vos informations de connexion :\n" +
                "Identifiant : " + username + "\n" +
                "Mot de passe : " + password + "\n" +
                "Nous restons à votre disposition pour toute assistance.\n" +
                "Cordialement,\n" +
                "L'équipe Leave Soft";

        // Créer un objet SmsRequest avec le numéro de téléphone et le message
        SmsRequest smsRequest = new SmsRequest(phoneNumber, messageSms);

        // Envoyer le SMS
        smsService.sendSms(smsRequest);
    }

    private String generateMatricule() {
        // Get the last matricule from the database
        String lastMatricule = userRepository.findTopByOrderByMatriculeDesc().getMatricule();
        // Increment the last matricule and return the new matricule value
        return incrementMatricule(lastMatricule);
    }

    private String incrementMatricule(String lastMatricule) {
        // Extract the numeric part of the matricule
        int number = Integer.parseInt(lastMatricule.substring(1));
        number++;

        // Format the incremented number with leading zeros
        return String.format("%04d", number);
    }



/*
    @Transactional
    public Personnel updatePersonnel(Long id, Personnel updatedPersonnel) {
        Personnel personnel = personnelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel does not exist with id: " + id));
        personnel.setNom(updatedPersonnel.getNom());
        personnel.setPrenom(updatedPersonnel.getPrenom());
        personnel.setSexe(updatedPersonnel.getSexe());
        personnel.setCin(updatedPersonnel.getCin());
        personnel.setService(updatedPersonnel.getService());
        personnel.setVille(updatedPersonnel.getVille());
       // personnel.setRole(updatedPersonnel.getRole());
        personnel.setTel(updatedPersonnel.getTel());
        personnel.setMatricule(updatedPersonnel.getMatricule());
        personnel.setNbrEnfant(updatedPersonnel.getNbrEnfant());
        personnel.setEmail(updatedPersonnel.getEmail());
        personnel.setLogin(updatedPersonnel.getLogin());
        personnel.setImage(updatedPersonnel.getImage());
        // Check if the password has changed
        if (!updatedPersonnel.getPassword().equals(personnel.getPassword())) {
            // Encode the new password before setting it
            personnel.setPassword(passwordEncoder.encode(updatedPersonnel.getPassword()));
        }
        return personnelRepo.save(personnel);
    }

*/
@Transactional
public Personnel modifierpersonnel(Personnel updateUser, Long id){
    Personnel user = userRepository.findById(id).orElse(null);
    user.setNom(updateUser.getNom());
    user.setPrenom(updateUser.getPrenom());
    user.setService(updateUser.getService());
    user.setStatut(updateUser.getStatut());
    user.setNbrEnfant(updateUser.getNbrEnfant());
    user.setTel(updateUser.getTel());
    user.setEmail(updateUser.getEmail());
    user.setMatricule(updateUser.getMatricule());
    user.setCin(updateUser.getCin());
    user.setSexe(updateUser.getSexe());
    user.setRole(updateUser.getRole());
  //  user.setLogin(updateUser.getLogin());
    return userRepository.save(user);
}


    public void deletePersonnel(Long id) {
        personnelRepo.deleteById(id);
    }




    private final PersonnelRepo repository;


    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (Personnel) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        //check if the current password is correct
       if(!passwordEncoder.matches(request.getCurrentPassword(),user.getPassword())){
      throw new IllegalStateException("wrong password");
        }

        //if the new password doesn't match the confirmation password
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
       //update password
       user.setPassword(passwordEncoder.encode(request.getNewPassword()));

      //save the new password
       repository.save(user);
    }




//search for personnel

    public Personnel findByQuery(String query) {
        // Check if the query is numeric (Cin or matricule)
        if (query.matches("\\d+")) {
            // Search by Cin or matricule
            return (Personnel) personnelRepo.findByCinOrMatricule(query, query);
        } else {
            // Search by name
            return (Personnel) personnelRepo.findByNom(query);
        }
    }
    public Personnel findByCinOrMatricule(String cin, String matricule) {
        return personnelRepo.findByCinOrMatricule(cin, matricule);
    }

    public List<Personnel> findByNom(String nom) {
        return (List<Personnel>) personnelRepo.findByNom(nom);
    }
    public List<Personnel> findByPrenom(String prenom) {
        return (List<Personnel>) personnelRepo.findByPrenom(prenom);
    }




    public Optional<Personnel> findById(Long id) {
        return  personnelRepo.findById(id);
    }


    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:4200")
                    .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH")
                    .allowedHeaders("*");}
    }



    //search conge
    @Autowired
    private CongeRepo congeRepo;

    public List<Conge> searchCongesByPersonnel(String query) {
        List<Conge> conges = congeRepo.findByPersonnelQuery(query);
        return conges;
    }

    public List<Conge> searchCongesByType(TypeConge type) {
        List<Conge> conges = congeRepo.findByType(type);
        return conges;
    }




    //count chef et employe
    public long countChefs() {
        return personnelRepo.countByRole(ROLe.CHEF);
    }
    public long countEmployes() {
        return personnelRepo.countByRole(ROLe.EMPLOYEE);
    }


    public List<CountRole> getCountGroupByRole() {
        return personnelRepo.getCountGroupByRole();
    }

    public List<CountRole> getPercentageGroupByRole() {
        return personnelRepo.getPercentageGroupByRole();
    }

    public List<CountSexe> getPercentageGroupBySexe() {
        return personnelRepo.getPercentageGroupBySexe();
    }

    public List<CountSexe> getPercentageGroupBySexeForEmployees() {
        return personnelRepo.getPercentageEmployeesex();
    }





}











