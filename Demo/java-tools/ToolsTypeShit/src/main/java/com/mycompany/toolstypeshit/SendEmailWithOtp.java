/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toolstypeshit;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author ddori
 */
public class SendEmailWithOtp {
    /**
     * Sends an OTP email using SMTP
     * @param recipientEmail The email address to send to
     * @param smtpHost SMTP server host (e.g., "smtp.gmail.com")
     * @param smtpPort SMTP server port (e.g., "587" for TLS)
     * @param senderEmail Your email address
     * @param senderPassword Your email password or app-specific password
     * @return The generated OTP code
     * @throws MessagingException if email sending fails
     */
    public static int sendOTPEmail(String recipientEmail, String smtpHost, 
                                    String smtpPort, String senderEmail, 
                                    String senderPassword, String fromAddress) throws MessagingException {
        
        // Generate random 6-digit OTP
        Random random = new Random();
        int otp = random.nextInt(900000) + 100000;
        
        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        
        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        
        // Create email message
        Message message = new MimeMessage(session);
        
        // Set the "From" header to your alias
        message.setFrom(new InternetAddress(fromAddress));
        
        message.setRecipients(Message.RecipientType.TO, 
                            InternetAddress.parse(recipientEmail));
        
        message.setSubject("Your OTP Code");
        
        // Create HTML content
        String htmlContent = "<!DOCTYPE html>" +
                           "<html>" +
                           "<body>" +
                           "<h1>" + otp + "</h1>" +
                           "</body>" +
                           "</html>";
        
        message.setContent(htmlContent, "text/html; charset=utf-8");
        
        // Send the email
        Transport.send(message);
        
        return otp;
    }
    
    // Example usage
    public static void main(String[] args) {
        try {
            int otp = sendOTPEmail(
                "example@gmail.com",      // Recipient email
                "smtp.gmail.com",              // SMTP host
                "587",                         // SMTP port
                "<redacted>@gmail.com",        // Your email
                System.getenv("SMTP_PSW"), // Your password
                "noreply@<redacted>"
            );
            
            System.out.println("OTP sent successfully: " + otp);
            
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
