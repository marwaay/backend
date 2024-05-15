package com.pfe.personnel.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest  {
    private String id;
    private String nom;
    private String prenom;
    private String login;
    private String password;
    private String cin;
    private String service;
    private String role;
    private String sexe;
    private String tel;
    private String email;
    private Integer nbrEnfant;
    private String matricule;
    private String statut;





}
