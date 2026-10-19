//package com.mfsys.crud.services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendPasswordResetOtp(String email, String otp) {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setTo(email);
//
//        message.setSubject( "Student Management System - Password Reset OTP" );
//
//        message.setText(
//                "Hello,\n\n" +
//                        "You requested a password reset for your Student Management System account.\n\n" +
//                        "Your password reset OTP is:\n\n" +
//                        otp + "\n\n" +
//                        "This OTP is valid for 5 minutes.\n\n" +
//                        "If you did not request a password reset, please ignore this email.\n\n" +
//                        "Regards,\n" +
//                        "Student Management System"
//        );
//
//        mailSender.send(message);
//    }
//}
//





package com.mfsys.crud.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient = RestClient.create();

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    public void sendPasswordResetOtp(String email, String otp) {

        String text =
                "Hello,\n\n" +
                        "You requested a password reset for your Student Management System account.\n\n" +
                        "Your password reset OTP is:\n\n" +
                        otp + "\n\n" +
                        "This OTP is valid for 5 minutes.\n\n" +
                        "If you did not request a password reset, please ignore this email.\n\n" +
                        "Regards,\n" +
                        "Student Management System";

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", "MFSYS", "email", senderEmail),
                "to", List.of(Map.of("email", email)),
                "subject", "Student Management System - Password Reset OTP",
                "textContent", text
        );

        restClient.post()
                .uri("https://api.brevo.com/v3/smtp/email")
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}