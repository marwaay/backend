package com.pfe.personnel.repository;

import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.models.ROLe;
import com.pfe.personnel.models.dto.CountRole;
import com.pfe.personnel.models.dto.CountSexe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRepo extends JpaRepository<Personnel, Long> {
    Optional<Personnel> findByEmail(String email);

    List<Personnel> findByRole(ROLe roLe);

    long countByRole(ROLe role);

    Optional<Personnel> findById(Long id);

    Optional<Personnel> findByLogin(String login);

    Personnel findTopByOrderByMatriculeDesc();

    List<Personnel> findByNom(String nom);

    List<Personnel> findByPrenom(String prenom);

    Personnel findByCinOrMatricule(String cin, String matricule);


    List<Personnel> findByNomOrPrenomOrCinOrMatricule(String nom, String prenom, String cin, String matricule);


    @Query(value = "SELECT new com.pfe.personnel.models.dto.CountRole(COUNT(*), role) FROM Personnel GROUP BY role")
    List<CountRole> getCountGroupByRole();


    @Query(value = "select new com.pfe.personnel.models.dto.CountRole(COUNT(*)/(SELECT COUNT(*) FROM Personnel) * 100, role) FROM Personnel GROUP BY role")
    List<CountRole> getPercentageGroupByRole();

    @Query(value = "select new com.pfe.personnel.models.dto.CountSexe(COUNT(*)/(SELECT COUNT(*) FROM Personnel) * 100, sexe) FROM Personnel GROUP BY sexe")
    List<CountSexe>getPercentageGroupBySexe();



    @Query("SELECT new com.pfe.personnel.models.dto.CountSexe(COUNT(*), p.sexe) " +
            "FROM Personnel p " +
            "WHERE p.role = 'EMPLOYEE' " +
            "GROUP BY p.sexe")
    List<CountSexe> getPercentageEmployeesex();

}