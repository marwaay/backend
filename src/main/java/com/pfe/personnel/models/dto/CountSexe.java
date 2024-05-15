package com.pfe.personnel.models.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountSexe {
    private  Long count;
    @ManyToOne
    @JoinColumn(name = "sexe")
    private String sexe;
}
