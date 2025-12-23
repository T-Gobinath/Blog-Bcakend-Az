package com.chatee.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-actual-email@gmail.com"); // Must match application.properties
            message.setTo(toEmail);
            message.setSubject("Chatee - Verify your email");
            message.setText("Your OTP is: " + code);

            System.out.println("Attempting to send email to: " + toEmail); // LOG 1
            mailSender.send(message);
            System.out.println("Email sent successfully!"); // LOG 2

        } catch (Exception e) {
            // THIS IS THE IMPORTANT PART
            System.err.println("EMAIL SENDING FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}