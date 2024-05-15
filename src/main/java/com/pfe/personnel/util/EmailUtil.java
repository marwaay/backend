package com.pfe.personnel.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    /*
        public void sendOtpEmail(String email, String otp) throws MessagingException {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("Arab Soft Verify Password");
            mimeMessageHelper.setText("""
            <div>
              <a href="http://localhost:8080/verify-account?email=%s&otp=%s" target="_blank">click link to verify</a>
            </div>
            """.formatted(email, otp), true);

            javaMailSender.send(mimeMessage);
        }
*/
    /*

        public void sendSetPasswordEmail(String email) throws MessagingException {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("Arab Soft Verify Password");
            mimeMessageHelper.setText("""
            <div>
              <a href="http://localhost:4200/mail/set-password" target="_blank">click link to set password</a>
            </div>
            """.formatted(email), true);

            javaMailSender.send(mimeMessage);

        }*/
    public void sendSetPasswordEmail(String email, String resetLink) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Arab Soft Password Reset");

        String textContent = """
        <p>Bonjour,</p>
        <p>Vous avez demandé à réinitialiser votre mot de passe.</p>
        <p>Cliquez sur le lien suivant pour réinitialiser votre mot de passe :</p>
        <a href="%s">%s</a>
        <p>Si vous n'avez pas demandé cela, veuillez ignorer ce message. Votre mot de passe ne sera pas modifié.</p>
        <p>Cordialement,</p>
        """.formatted(resetLink, resetLink);

        mimeMessageHelper.setText(textContent, true);

        javaMailSender.send(mimeMessage);
    }

}





