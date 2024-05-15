package com.pfe.personnel.models.dto;


import com.pfe.personnel.models.TypeConge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CountType {
    private  Long count;
    private TypeConge type;



}
