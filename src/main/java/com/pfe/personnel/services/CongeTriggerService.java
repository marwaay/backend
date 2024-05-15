package com.pfe.personnel.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CongeTriggerService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CongeTriggerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void createCongeTrigger() {
        // Check if the trigger already exists
        String triggerCheckQuery = "SELECT 1 FROM information_schema.triggers WHERE trigger_name = 'demande_conge_trigger'";
        List<Integer> triggerList = jdbcTemplate.queryForList(triggerCheckQuery, Integer.class);

        // If the trigger does not exist, create it
        if (triggerList.isEmpty()) {
            String triggerQuery = "CREATE TRIGGER demande_conge_trigger AFTER INSERT ON conge " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "DECLARE chef_id INT; " +
                    "DECLARE notification_subject VARCHAR(255); " +
                    "DECLARE notification_content TEXT; " +
                    "SELECT id INTO chef_id FROM personnels WHERE role = 'CHEF' AND service = " +
                    "(SELECT service FROM personnels WHERE id = NEW.user_id); " +
                    "SET notification_subject = 'Demande de congé'; " +
                    "SET notification_content = CONCAT(" +
                    "'Type : ', NEW.type, '\n', " +
                    "'Date de demande : ', NEW.date_dem, '\n', " +
                    "'Date de début : ', NEW.date_deb, '\n', " +
                    "'Date de fin : ', NEW.date_fin, '\n', " +
                    "'Durée : ', NEW.durée, '\n', " +
                    "'Statut : ', NEW.statut, '\n', " +
                    (isNewExceptionalConge() ?
                            "'Motif : ', NEW.motif, '\n', " :
                            "") +
                    "'File : ', NEW.file, '\n'); " +
                    "INSERT INTO notification (sender_id, recipient_id, sent_at, subject, content,lu) " +
                    "VALUES (NEW.user_id, chef_id, NOW(), notification_subject, notification_content,0); " +
                    "END;";
            jdbcTemplate.execute(triggerQuery);
        }




    }







        private boolean isNewExceptionalConge() {
        return false;
    }
}