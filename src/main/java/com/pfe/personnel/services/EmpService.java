package com.pfe.personnel.services;

import com.pfe.personnel.models.Conge;
import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.models.TypeConge;
import com.pfe.personnel.repository.CongeRepo;
import com.pfe.personnel.repository.PersonnelRepo;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class EmpService {


    @Autowired
    private CongeRepo congeRepo;

    @Autowired
    private MailService emailService;

    @Autowired
    PersonnelRepo personnelRepo;
/*
    public Conge ajouterconge(Conge conge) {
        return this.congeRepo.save(conge);
    }

    */

    public List<Conge> afficherconges() {
        return congeRepo.findAll();
    }

    public Optional<Conge> afficherconge(int id) {
        return congeRepo.findById((long) id);
    }


    @Autowired
    PersonnelService personnelService;

    public Conge ajouterConge(Long userId, String typeConge) {
        // Fetch the Personnel object by user ID
        Optional<Personnel> personnelOptional = personnelService.findById(userId);
        if (personnelOptional.isEmpty()) {
            // personnel not found
            return null;
        }

        Personnel personnel = personnelOptional.get();

        // maternite
        int dureeConge = 0;
        if (typeConge.equals("MATERNITE")) {
            dureeConge = calculerDureeCongeMaternite(personnel);
        }

        Conge conge = new Conge();
        conge.setUser(personnel);
        conge.setType(TypeConge.valueOf(typeConge));
        conge.setDuree(dureeConge);


        return congeRepo.save(conge);
    }


    public int calculerDureeCongeMaternite(Personnel personnel) {
        int nombreEnfants = personnel.getNbrEnfant();
        int dureeCongeMaternite = 0;


        if (nombreEnfants == 1 || nombreEnfants == 2) {
            dureeCongeMaternite = 16 * 7;
        } else if (nombreEnfants >= 3) {
            dureeCongeMaternite = 26 * 7;
        }

        return dureeCongeMaternite;
    }


    public Long getChefIdByUserService(Long userId) {
        Personnel user = personnelRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Personnel> chefs = personnelService.getPersonnelByRole("CHEF");

        for (Personnel chef : chefs) {
            if (chef.getService() != null && chef.getService().equals(user.getService())) {
                return chef.getId();
            }
        }

        throw new RuntimeException("Chef not found for user's service");
    }
    public Long getEmpIdByUserService(Long chefId) {
        Personnel user = personnelRepo.findById(chefId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + chefId));

        List<Personnel> employes = personnelService.getPersonnelByRole("EMPLOYEE");

        for (Personnel employe : employes) {
            if (employe.getService() != null && employe.getService().equals(user.getService())) {
                return employe.getId();
            }
        }

        throw new RuntimeException("Employe not found for user's service");
    }
}


