package com.pfe.personnel.services;

import com.pfe.personnel.models.Notification;
import com.pfe.personnel.repository.NotifRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class NotifService {
    @Autowired
    private NotifRepo notRepo;

    private JdbcTemplate jdbcTemplate;


    public List<Notification> affichernotifications(){
        return notRepo.findAll();
    }

    public String getSenderEmailFromNotification(Long notificationId) {
        return notRepo.findSenderEmailByNotificationId(notificationId);
    }
    public String getRecipEmailFromNotification(Long notificationId) {
        return notRepo.findRecipEmailByNotificationId(notificationId);
    }



    @Autowired
    public NotifService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Notification getLatestNotification() {
        String query = "SELECT * FROM notification ORDER BY id DESC LIMIT 1";
        return jdbcTemplate.queryForObject(query, new NotificationRowMapper());
    }



    private class NotificationRowMapper implements RowMapper<Notification> {
        @Override
        public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
            Notification notification = new Notification();
            notification.setId(rs.getLong("id"));
            notification.setSender_id(rs.getLong("sender_id"));
            notification.setRecipient_id(rs.getLong("recipient_id"));
            notification.setSubject(rs.getString("subject"));
            notification.setContent(rs.getString("content"));
            return notification;
        }
    }

    public void supprimernotification (Long idnot){
        notRepo.deleteById(idnot);
    }

    public Optional<Notification> affichernotification(Long id) {
        return notRepo.findById(id);
    }

    public long countNotificationsLu() {
        return notRepo.countByLu(1);
    }
    }