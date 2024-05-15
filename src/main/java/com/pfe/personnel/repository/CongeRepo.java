package com.pfe.personnel.repository;

import com.pfe.personnel.models.Conge;
import com.pfe.personnel.models.TypeConge;
import com.pfe.personnel.models.dto.CountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CongeRepo extends JpaRepository<Conge,Integer> {

    Optional<Conge> findById(Long congeId);


    @Query("SELECT COUNT(c) FROM Conge c WHERE c.user.service = :service AND c.statut = :statut " +
            "AND ((c.date_debut <= :endDate AND c.date_fin >= :startDate) OR " +
            "(c.date_debut >= :startDate AND c.date_fin <= :endDate) OR " +
            "(c.date_debut <= :startDate AND c.date_fin >= :endDate))")
    long countOverlappingLeaveRequests(@Param("service") String service,
                                       @Param("startDate") Date startDate,
                                       @Param("endDate") Date endDate,
                                       @Param("statut") String statut);


    @Query("SELECT COUNT(c) > 0 FROM Conge c WHERE c.user.service = :service AND c.id <> :congeId AND c.date_fin >= :startDate AND c.date_debut <= :endDate")
    boolean hasOverlappingLeaveRequestsForOtherPersonnel(@Param("service") String service, @Param("congeId") int congeId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    @Query("SELECT c FROM Conge c WHERE c.user.nom LIKE %:query% OR c.user.prenom LIKE %:query% OR c.user.cin LIKE %:query% OR c.user.matricule LIKE %:query%")
    List<Conge> findByPersonnelQuery(@Param("query") String query);
    List<Conge> findByType(TypeConge typeConge);



    //chart js
    @Query(value = "select * from Conge order by date_deb desc", nativeQuery = true)
    public List<Conge> getAllDateDebut();
//jpa query
@Query(value = "select new com.pfe.personnel.models.dto.CountType(COUNT(*)/(Select COUNT(*) from Conge) * 100, type) from Conge GROUP BY type")
public List<CountType> getPercentageGroupByType();



    @Query(value = "select new com.pfe.personnel.models.dto.CountType(COUNT(*)/(Select COUNT(*) from Conge) * 100, c.type) from Conge c where c.user.id = :userId and c.statut = 'Confirmé' GROUP BY c.type")
    public List<CountType> getPercentageGroupByType(@Param("userId") Long userId);

    //all users count conges
    @Query("SELECT c.user.id, COUNT(c) FROM Conge c GROUP BY c.user.id")
    List<Object[]> countCongesByUser();

    //chaque user get his own conges nmbr
    @Query("SELECT COUNT(c) FROM Conge c WHERE c.user.id = :userId")
    Long countCongesByUser(@Param("userId") Long userId);


    @Query(value = "SELECT COUNT(*) as count, (COUNT(*) / (SELECT COUNT(*) FROM Conge c2 WHERE c2.user_id = :userId) * 100) as percentage FROM Conge c WHERE c.user_id = :userId AND c.statut = 'Confirmé'", nativeQuery = true)
    List<Object[]> getCountAndPercentageOfConfirmedCongesByUser(@Param("userId") Long userId);



    @Query("SELECT " +
            "SUM(CASE WHEN c.statut = 'En_Attente' THEN 1 ELSE 0 END) AS enAttenteCount, " +
            "SUM(CASE WHEN c.statut = 'Confirmé' THEN 1 ELSE 0 END) AS confirmeCount, " +
            "SUM(CASE WHEN c.statut = 'Refusé' THEN 1 ELSE 0 END) AS refuseCount " +
            "FROM Conge c WHERE c.user.id = :userId GROUP BY c.user.id")
    List<Object[]> countCongesBystatut(@Param("userId") Long userId);




}




