package com.pfe.personnel.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Conge")
public class Conge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Enumerated(EnumType.STRING)
    private TypeConge type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_dem")
    private Date date_demande;

    @PrePersist
    public void setDate_demande() {
        this.date_demande = new Date();
    }

    @Column(name = "date_deb")
    private Date date_debut;

    @Column(name = "date_fin")
    private Date date_fin;

    @Column(name = "durée")
    private int duree;

    @Column(name = "statut")
    private String statut;


    @Column(name = "motif")
    private String motif;

    @Column(name = "description")
    private String description;

    @Column(name = "file")
    private String file;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Personnel user;


    @Column(name = "chef_id")
    private Long chefId;



}

