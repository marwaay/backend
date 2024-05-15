
package com.pfe.personnel.controllers;

import com.pfe.personnel.auth.AuthenticationResponse;
import com.pfe.personnel.auth.RegisterRequest;
import com.pfe.personnel.models.Conge;
import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.models.TypeConge;
import com.pfe.personnel.models.dto.CountRole;
import com.pfe.personnel.models.dto.CountSexe;
import com.pfe.personnel.password.ChangePasswordRequest;
import com.pfe.personnel.repository.CongeRepo;
import com.pfe.personnel.services.PersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")

@RequestMapping("/personnels")

public class PersonnelController {
    @Autowired
    private PersonnelService personnelService;

    public PersonnelController(PersonnelService personnelService, PersonnelService service) {
        this.personnelService = personnelService;
        this.service = service;
    }
    private final PersonnelService service;





    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }








    @GetMapping("/personnellist")
    public List<Personnel> showPersonnelList() {
        return personnelService.afficher();
    }

    @PostMapping("/personnellist")
    public void createPersonnel(@RequestBody Personnel personnel) {

    }

    @GetMapping("/generatePassword")
    public String generatePassword() {
        return PersonnelService.generatePassword(10);
    }


    //@PostMapping("/ajouter")
    //public Personnel ajouterPersonnel(@RequestBody Personnel personnel) {
    //  return personnelService.ajouterPersonnel(personnel);}
    @PostMapping("/ajouter")
    public ResponseEntity<AuthenticationResponse> ajouterPersonnel(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(personnelService.ajouterPersonnel(request));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Personnel> getPersonnelById(@PathVariable Long id) {
        Personnel personnel = personnelService.getPersonnelById(id);
        return ResponseEntity.ok(personnel);
    }

    @PutMapping("/modifieruser/{id}")
    public ResponseEntity<Personnel> modifierpersonnel(@RequestBody Personnel user, @PathVariable Long id) {
        Personnel updateuser= personnelService.modifierpersonnel(user,id);
        return ResponseEntity.ok(updateuser);
    }


    @DeleteMapping("/delete/{id}")
    public void deletePersonnel(@PathVariable Long id) {
        personnelService.deletePersonnel(id);
    }


    @GetMapping("/allchef")
    public ResponseEntity<List<Personnel>> getAllChefs() {
        List<Personnel> chefList = personnelService.getPersonnelByRole("CHEF");
        return ResponseEntity.ok(chefList);
    }

    @GetMapping("/allemployee")
    public ResponseEntity<List<Personnel>> getAllEmployees() {
        List<Personnel> employeeList = personnelService.getPersonnelByRole("EMPLOYEE");
        return ResponseEntity.ok(employeeList);
    }


    @GetMapping("/searchuser")
    public ResponseEntity<?> getPersonnelDetails(@RequestParam(required = false) String query) {
        if (query != null && !query.isEmpty()) {
            // recherche cin nom matricule
            List<Personnel> personnelList = new ArrayList<>();

            if (query.matches("\\d+")) {
                // si nombre rechrche cin matricule
                Personnel personnel = personnelService.findByCinOrMatricule(query, query);
                if (personnel != null) {
                    personnelList.add(personnel);
                }
            } else {
                // nom
                personnelList.addAll(personnelService.findByNom(query));
            }

            if (!personnelList.isEmpty()) {
                // Return personnel details list
                return ResponseEntity.ok(personnelList);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No personnel found.");
            }
        } else {
            return ResponseEntity.badRequest().body("Query parameter is required.");
        }
    }


    @GetMapping("/searchconge")
    public ResponseEntity<List<Conge>> searchCongesByPersonnel(
            @RequestParam(required = false) String query) {

        List<Conge> congés;

        if (query == null || query.isEmpty()) {
            // Aucune query fournie
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }

        // Essayer de convertir la query en type de congé
        TypeConge typeConge = null;
        try {
            typeConge = TypeConge.valueOf(query.toUpperCase());
        } catch (IllegalArgumentException e) {
            // La query n'est pas un type de congé valide
        }

        if (typeConge != null) {
            // Recherche par type
            congés = personnelService.searchCongesByType(typeConge);
        } else {
            // Recherche par nom, prénom, CIN ou matricule
            congés = congeRepo.findByPersonnelQuery(query);
        }

        if (congés.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(congés);
        }
    }




    @Autowired
    CongeRepo congeRepo;

    @GetMapping("/nbrchefs")
    public Long nombrechef()
    {
        Long  nbrchefs=  personnelService.countChefs();
        return  nbrchefs;
    }
    @GetMapping("/nbremployes")
    public Long nombreemploye()
    {
        Long  nbremployes= personnelService.countEmployes();
        return  nbremployes;
    }



    @GetMapping("/countByRole")
    public List<CountRole> getCountGroupByRole() {
        return personnelService.getCountGroupByRole();
    }

    @GetMapping("/percentageByRole")
    public List<CountRole> getPercentageGroupByType() {
        return personnelService.getPercentageGroupByRole();
    }

    @GetMapping("/percentageBySexe")
    public List<CountSexe> getPercentageGroupBySexe() {
        return personnelService.getPercentageGroupBySexe();
    }




    @GetMapping("/sexpercentEmployees")
    public ResponseEntity<List<CountSexe>> getPercentageBySexeForEmployees() {
        List<CountSexe> percentages = personnelService.getPercentageGroupBySexeForEmployees();
        return new ResponseEntity<>(percentages, HttpStatus.OK);
    }
}









