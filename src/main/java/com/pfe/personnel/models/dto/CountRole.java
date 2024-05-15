package com.pfe.personnel.models.dto;

import com.pfe.personnel.models.ROLe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountRole {
    private Long count;
    private ROLe type;

}

