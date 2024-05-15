package com.pfe.personnel.repository;

import com.pfe.personnel.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface NotifRepo extends JpaRepository<Notification, Long> {


    @Query("SELECT p.email FROM Personnel p JOIN Notification n ON p.id = n.sender_id " +
            "WHERE n.sender_id = :notificationId AND p.role = com.pfe.personnel.models.ROLe.EMPLOYEE " +
            "AND n.sent_at = (SELECT MAX(n2.sent_at) FROM Notification n2 WHERE n2.sender_id = n.sender_id)")
    String findSenderEmailByNotificationId(@Param("notificationId") Long notificationId);


    @Query("SELECT p.email FROM Personnel p JOIN Notification n ON p.id = n.recipient_id " +
            "WHERE n.recipient_id = :notificationIdR AND p.role = com.pfe.personnel.models.ROLe.CHEF " +
            "AND n.sent_at = (SELECT MAX(n3.sent_at) FROM Notification n3 WHERE n3.recipient_id = n.recipient_id)")
    String findRecipEmailByNotificationId(@Param("notificationIdR") Long notificationIdR);

    long countByLu(int i);
}
