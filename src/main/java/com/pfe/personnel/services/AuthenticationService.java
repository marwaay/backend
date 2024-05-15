package com.pfe.personnel.services;

import com.pfe.personnel.auth.AuthenticationRequest;
import com.pfe.personnel.auth.AuthenticationResponse;
import com.pfe.personnel.auth.RegisterRequest;
import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.models.ROLe;
import com.pfe.personnel.repository.PersonnelRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PersonnelRepo userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


 public AuthenticationResponse login(AuthenticationRequest request) {
       authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
       UserDetails user = userRepository.findByLogin(request.getLogin()).orElseThrow();
       String token = jwtService.getToken(user);
       return AuthenticationResponse.builder()
               .token(token)
               .build();

}


    public AuthenticationResponse register(RegisterRequest request) {
        Long id = request.getId() != null ? Long.valueOf(request.getId()) : null;
        ROLe role;

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
        Personnel user =Personnel.builder()

                .password(passwordEncoder.encode( request.getPassword()))
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .service(request.getService())
                .cin(request.getCin())
                .tel(request.getTel())
                .sexe(request.getSexe())
                .nbrEnfant(request.getNbrEnfant())
                .sexe(request.getSexe())
                .email(request.getEmail())
                .statut(request.getStatut())
                .id(id)
                .role(role)
                .solde(60)
                .build();
        user.setMatricule(generateMatricule());
     user.setLogin(user.getMatricule());
        return AuthenticationResponse.builder()
                .token(jwtService.getToken(user))
                .build();

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

    @Autowired
    private PersonnelRepo personnelRepository;


    public Optional<Personnel> getUserByUsername(String login) {
        return personnelRepository.findByLogin(login);
    }
    public Personnel getUserById(Long id) {
        return personnelRepository.findById(id).orElse(null);
    }

















}


