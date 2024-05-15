package com.pfe.personnel.services;

import com.pfe.personnel.models.Personnel;
import com.pfe.personnel.repository.PersonnelRepo;
import com.pfe.personnel.util.EmailUtil;
import com.pfe.personnel.util.OtpUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MailService {
    @Autowired
    private OtpUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private PersonnelRepo userRepository;

    @Autowired
    private SmsService smsService;
    /*
   public String forgotPassword(String email) {
        Personnel user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found with this email" + email));
        try {
            String resetLink = "http://localhost:4200/mail/set-password" ;

            emailUtil.sendSetPasswordEmail(email);
            smsService.envoyerSMSResetPWD(user.getTel(), resetLink);

        } catch (MessagingException e) {
            throw new RuntimeException("Unable to set password email please try again");
        }
        return " check your email ";
    }
*/

    public String forgotPassword(String email) {
        Personnel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email" + email));

        // Generate a unique token
        String token = generateToken();

        // Set token expiration time (e.g., 1 hour from now)
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(1);

        // Store token in the database along with user's ID and expiration time


        try {
            // Include token in the reset link
            String resetLink = "http://localhost:4200/mail/set-password?token=" + token;

            // Send the password reset email
            emailUtil.sendSetPasswordEmail(email, resetLink);

        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send password reset email. Please try again.");
        }
        return "Check your email for the password reset link.";
    }

    // Method to generate a unique token
    private String generateToken() {
        // Implement your token generation logic here (e.g., using UUID)
        return UUID.randomUUID().toString();
    }
    public String setPassword(@RequestParam String email,
                              @RequestHeader String newPassword,
                              @RequestHeader String confirmPassword) {
        Personnel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        if (!newPassword.equals(confirmPassword)) {
            return "Password and confirm password do not match";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
        userRepository.save(user); // Save the updated user object with the new password

        return "New password set successfully, login with the new password";
    }


    public String changePassword(@RequestHeader String email,
                                 @RequestHeader String oldPassword,
                                 @RequestHeader String newPassword,
                                 @RequestHeader String confirmPassword) {

        Personnel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec cet e-mail: " + email));


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return "Ancien mot de passe incorrect";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "Le nouveau mot de passe et la confirmation ne correspondent pas";
        }


        String encodedPassword = passwordEncoder.encode(newPassword);


        user.setPassword(encodedPassword);
        userRepository.save(user);

        return "Nouveau mot de passe défini avec succès, connectez-vous avec le nouveau mot de passe";
    }




    //maram code
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String from,String to,  String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        mimeMessage.setContent(body, "text/html");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        javaMailSender.send(mimeMessage);
    }

    public void sendConfirmationEmail(String email, String congéConfirmé, String s) {
    }


}
