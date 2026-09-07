package com.mfsys.crud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText(
                "You requested a password reset.\n\n" +
                        "Click the link below to reset your password (valid for 15 minutes):\n" +
                        resetLink + "\n\n" +
                        "If you did not request this, please ignore this email."
        );
        mailSender.send(message);
    }
}