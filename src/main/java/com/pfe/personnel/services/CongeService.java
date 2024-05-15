package com.pfe.personnel.services;

import com.pfe.personnel.models.Conge;
import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.models.dto.CountType;
import com.pfe.personnel.repository.CongeRepo;
import com.pfe.personnel.repository.PersonnelRepo;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CongeService {

    @Autowired
    private CongeRepo congeRepo;

    @Autowired
    private MailService emailService;


    @Autowired
    private PersonnelRepo personnelRepo;


  //  private final SmsService smsService;

  //  @Autowired
 //   public CongeService(SmsService smsService) {
       // this.smsService = smsService;
  //  }

    public Conge refuseConge(Long congeId) {
        Optional<Conge> optionalConge = congeRepo.findById(congeId);
        Conge conge = optionalConge.get();
        conge.setStatut("Refusé");

        Personnel personnel = conge.getUser();

        try {
            monitorSolde(personnel);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return congeRepo.save(conge);
    }

    public Conge confirmerConge(Long congeId) {
        Optional<Conge> optionalConge = congeRepo.findById(congeId);
        Conge conge = optionalConge.get();
        conge.setStatut("Confirmé");

        updateLeaveBalanceAfterApproval(congeId);
        Personnel personnel = conge.getUser();

        try {
            monitorSolde(personnel);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return congeRepo.save(conge);
    }


    @Autowired
    private PersonnelRepo employeeRepository;

    public void updateLeaveBalanceAfterApproval(Long congeId) {
        // Retrieve the leave request from the database

        Conge conge = congeRepo.findById(congeId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        // Check if the leave request is approved
        if (conge.getStatut().equals("Confirmé")) {
            // Retrieve the user associated with the leave request
            Personnel user = conge.getUser();

            // Reduce the leave balance of the user
            user.setSolde(user.getSolde() - conge.getDuree());
            employeeRepository.save(user);
        }

    }

    public List<Conge> getArchive() {
        // Récupérer tous les congés
        List<Conge> allConges = congeRepo.findAll();

        // Filtrer les congés acceptés ou refusés
        List<Conge> archiveConges = allConges.stream()
                .filter(conge -> conge.getStatut().equals("Confirmé") || conge.getStatut().equals("Refusé"))
                .collect(Collectors.toList());

        return archiveConges;
    }

    public Personnel getuser(Long congeId) {
        Optional<Conge> optionalConge = congeRepo.findById(congeId);
        Conge conge = optionalConge.get();
        return conge.getUser();
    }


    public void monitorSolde(Personnel personnel) throws MessagingException {
        if (personnel.getSolde() < 0) {
            sendSanctionEmail(personnel.getEmail(), personnel.getNom(), personnel.getMatricule());
          //  sendSanctionSms(personnel.getTel(), personnel.getNom(), personnel.getMatricule());
        } else if (personnel.getSolde() < 30) {
            sendAnnuelEmail(personnel.getEmail(), personnel.getNom(), personnel.getMatricule());
          //  sendAnnuelSms(personnel.getTel(), personnel.getNom(), personnel.getMatricule());
        }
    }
/*
    public void sendAnnuelSms(String phoneNumber, String nom, String matricule) {
        smsService.sendAnnuelSms(phoneNumber, nom, matricule);
    }

    public void sendSanctionSms(String phoneNumber, String nom, String matricule) {
        smsService.sendSanctionSms(phoneNumber, nom, matricule);
    }
*/
   private void sendAnnuelEmail(String to, String nom, String matricule) throws MessagingException {
        String from = "marwa9raya@gmail.com";
        String subject = "Notification de congés";
        String body = "Bonjour " + nom + ",\n\n" + "de matricule: " + (matricule != null ? matricule : "Non disponible") + "\n\n" +
                "Nous tenons à vous informer que votre solde de congé annuel pour cette année est terminé et que le solde de congé exceptionnel a commencé." +
                " Veuillez noter que cela est conforme aux politiques de l'entreprise.\n\n" +
                "Cordialement,\n" +
                "Leave Soft";

        emailService.sendEmail(from, to, subject, body);
    }

    private void sendSanctionEmail(String to, String nom, String matricule) throws MessagingException {
        String from = "marwa9raya@gmail.com";
        String subject = "Sanction Notification";
        String body = "Bonjour " + nom + ",\n\n" + "de matricule: " + (matricule != null ? matricule : "Non disponible") + "\n\n" +
                "Nous tenons à vous informer que votre solde de congé est tombé en dessous de zéro. Veuillez noter que cela est contraire aux politiques de l'entreprise.\n\n" +

                "Nous vous demandons de corriger cette situation dès que possible en ajustant votre utilisation du congé conformément aux directives de l'entreprise.\n\n" +
                "Cordialement,\n" +
                "Leave Soft";

        emailService.sendEmail(from, to, subject, body);
    }

    public List<Conge> getCongesConfirmes() {
        // Récupérer tous les congés
        List<Conge> allConges = congeRepo.findAll();

        // Filtrer les congés acceptés
        List<Conge> Conges = allConges.stream()
                .filter(conge -> conge.getStatut().equals("Confirmé") )
                .collect(Collectors.toList());

        return Conges;
    }

    public List<Conge>getconges()
    {
        return congeRepo.getAllDateDebut();
    }

    public List<CountType>getPercentageGroupByType()
    {
        return congeRepo.getPercentageGroupByType();
    }

    public List<CountType> getPercentageGroupByTypeForUser(Long userId) {
        return congeRepo.getPercentageGroupByType(userId);
    }



    public Map<Long, Long> countCongesByUser() {
        List<Object[]> results = congeRepo.countCongesByUser();


        return results.stream()
                .collect(Collectors.toMap(
                        array -> (Long) array[0],
                        array -> (Long) array[1]
                ));
    }



    public Long countCongesByUser(Long userId) {
        return congeRepo.countCongesByUser(userId);
    }




    public List<Object[]> getCountAndPercentageOfConfirmedCongesByUser(Long userId) {
        return congeRepo.getCountAndPercentageOfConfirmedCongesByUser(userId);
    }



    public List<Object[]> countCongesByStatut(Long userId) {
        return congeRepo.countCongesBystatut(userId);
    }


    }





