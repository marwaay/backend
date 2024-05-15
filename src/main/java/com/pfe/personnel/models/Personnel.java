package com.pfe.personnel.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="personnels")
public class Personnel implements UserDetails {
    // Getter method for user ID
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom" )
    private String nom;

    @Column(name = "solde")
    private Integer  solde;


    @Column(name = "prenom" )
    private String prenom;

    @Column(name = "cin")
    private String cin;

    @Column(name = "service")
    private String service;

    @Column(name = "role" )
    @Enumerated(EnumType.STRING)
    private ROLe  role;

    @Column(name = "statut")
    private String statut;
    @Column(name = "sexe")
    private String sexe;
    @Column(name = "tel" )
    private String tel;
    @Column(name = "email" )
    private String email;
    @Column(name = "nbr_enfant" )
   private Integer nbrEnfant;
    @Column(name = "login" )
    private String login;
    @Column(name = "password"    )
     private String  password;

    @Column(name = "matricule")
    private String matricule;

    public Personnel(Long id) {  this.id = id;}


    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

  //  private String otp;
   // private LocalDateTime otpGeneratedTime;

/*
    @Override

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }
    */


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return role.getAuthorities();
        } else {
            // Handle the case when the role is null, e.g., return an empty collection
            return Collections.emptyList();
        }
    }


    @Override
    public String getPassword() {
        return password;
    }







    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
