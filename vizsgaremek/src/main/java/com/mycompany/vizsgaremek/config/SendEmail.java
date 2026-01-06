/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.config;

import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.mycompany.vizsgaremek.model.Users;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ddori
 */
public class SendEmail {
    //ToDo: Verify OTP function
    /**
     * Sends an OTP email using SMTP
     *
     * @param recipientEmail The email address to send to
     * @param smtpHost SMTP server host (e.g., "smtp.gmail.com")
     * @param smtpPort SMTP server port (e.g., "587" for TLS)
     * @param senderEmail Your email address
     * @param senderPassword Your email password or app-specific password
     * @return The generated OTP code
     * @throws MessagingException if email sending fails
     */
    public static void sendOTPEmailAndSetAuthSecret(String recipientEmail, Users userdata) throws MessagingException {

// Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        KvFetcher.getDataFromKV("SMTPEmail"),
                        base64Converters.base64Converter(KvFetcher.getDataFromKV("SMTPPsw"))
                );
            }
        });

        // Create email message
        Message message = new MimeMessage(session);

        // Set the "From" header to your alias
        message.setFrom(new InternetAddress("noreply@carcomps.hu"));

        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(recipientEmail));

        message.setSubject("Carcomps - kétlépcsős azonosítás");

        // Generate random 6-digit OTP
        Random random = new Random();
        String OTP = Integer.toString(random.nextInt(900000) + 100000);

        // get username from recipient email
        String lastName = userdata.getLastName();

        // Create HTML content
        String htmlContent = "<!DOCTYPE html>"
                + "<html lang=\"en\">"
                + "<head>"
                + "<meta charset=\"UTF-8\" />"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />"
                + "<title>Kétlépcsős azonosítás</title>"
                + "<style>"
                + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                + "body { background: #111; padding: 20px; font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; }"
                + ".email-container { border-radius: 10px; max-width: 600px; width: 100%; margin: 20px auto; background-color: #2b2b2b; color: #fff; text-align: center; overflow: hidden; }"
                + ".email-header, .email-footer { padding: 16px; }"
                + ".email-header img { width: 300px; display: block; margin: auto; }"
                + ".email-body { padding: 0 24px 24px; text-align: left; }"
                + "hr { border: none; height: 1px; background: rgba(255, 255, 255, 0.06); margin: 20px 0; }"
                + "h2 { margin: 18px 0; color: #fffafa; font-size: 20px; text-align: left; }"
                + "p { margin: 0 0 16px; font-size: 16px; line-height: 1.6; color: #eaeaea; }"
                + ".auth-container { display: flex; justify-content: center; margin: 20px 0; }"
                + ".code { display: inline-block; font-family: 'Courier New', Courier, monospace; font-size: 22px; font-weight: 700; padding: 12px 28px; border-radius: 6px; background: #fffafa; color: #ff6600; letter-spacing: 0.25em; min-width: 160px; text-align: center; }"
                + ".email-footer p { color: #bfbfbf; font-size: 12px; margin: 0; text-align: center; }"
                + ".email-footer a { color: #9fc5ff; text-decoration: none; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"email-container\">"
                + "<div class=\"email-header\">"
                + "<img src=\"https://i.ibb.co/h1dS9kgd/Car-Comps-Logo-Bigass-C.png\" alt=\"CarComps logó\" />"
                + "</div>"
                + "<div class=\"email-body\">"
                + "<hr />"
                + "<h2>Kedves " + lastName + "!</h2>"
                + "<p>Az Ön fiókjának biztonsága érdekében kétlépcsős azonosítást használunk. Az alábbi egyszeri kódot kell megadnia a bejelentkezéshez:</p>"
                + "<div class=\"auth-container\">"
                + "<div class=\"code\"><span id=\"number\">" + OTP + "</span></div>"
                + "</div>"
                + "<p>Ez a kód csak egyszer használható, és <span style=\"text-decoration: underline\">10 percig</span> érvényes.</p>"
                + "<p>Ha Ön nem próbált bejelentkezni, kérjük, azonnal változtassa meg jelszavát, és ellenőrizze fiókja biztonsági beállításait.</p>"
                + "<p>Köszönjük, hogy biztonságban tartja fiókját!<br />CarComps csapata</p>"
                + "</div>"
                + "<hr />"
                + "<div class=\"email-footer\">"
                + "<p>Ez egy automatikus üzenet, kérjük, ne válaszolj rá.<br />© 2025 CarComps – Minden jog fenntartva.<br />Székhely: 7621 Pécs, Fő utca 12.<br />"
                + "<a href=\"https://carcomps.hu/adatvedelem\">Adatvédelmi nyilatkozat</a> | <a href=\"https://carcomps.hu/aszf\">Felhasználási feltételek</a></p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=utf-8");

        // Send the email
        Transport.send(message);

        //Update User's Auth Secret
        try {
            Users updateAuthUser = new Users();
            updateAuthUser.setAuthSecret(OTP);
            updateAuthUser.setId(userdata.getId());
            updateAuthUser.setEmail(userdata.getEmail());
            updateAuthUser.setUsername(userdata.getUsername());
            updateAuthUser.setFirstName(userdata.getFirstName());
            updateAuthUser.setLastName(userdata.getLastName());
            updateAuthUser.setPhone(userdata.getPhone());
            updateAuthUser.setIsActive(userdata.getIsActive());
            updateAuthUser.setIsSubscribed(userdata.getIsSubscribed());
            updateAuthUser.setPassword(userdata.getPassword());
            updateAuthUser.setRegistrationToken(userdata.getRegistrationToken());
            updateAuthUser.setRole("");
            Users.updateUser(updateAuthUser);
        } catch (Exception ex) {
            System.err.println("updateUser error: " + ex);
        }

        System.out.println("Email Sent and auth secret updated for user: " + lastName);
    }

    /**
     * Sends a promotion email using SMTP to every subscribed User with the
     * alias of "Promotion@Carcomps.hu" and the given message contents
     *
     * @param messageContent The HTML message content to send
     * @throws MessagingException if email sending fails
     */
    public static void sendPromotionEmailToSubscribedUsers(String messageContent) throws MessagingException {

        // Get all users
        ArrayList<Users> allUsers = Users.getUsers();

        if (allUsers == null || allUsers.isEmpty()) {
            System.out.println("No users found in database");
            return;
        }

        // Filter subscribed users
        ArrayList<String> subscribedEmails = new ArrayList<>();
        for (Users user : allUsers) {
            if (user.getIsSubscribed() != null && user.getIsSubscribed() && user.getEmail() != null) {
                subscribedEmails.add(user.getEmail());
            }
        }

        if (subscribedEmails.isEmpty()) {
            System.out.println("No subscribed users found");
            return;
        }

        System.out.println("Found " + subscribedEmails.size() + " subscribed users");

        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        KvFetcher.getDataFromKV("SMTPEmail"),
                        base64Converters.base64Converter(KvFetcher.getDataFromKV("SMTPPsw"))
                );
            }
        });

        // Create email message
        MimeMessage message = new MimeMessage(session);

        // Set the "From" header
        message.setFrom(new InternetAddress("promotion@carcomps.hu"));

        // Set subject
        message.setSubject("test");

        // Create HTML content
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body>"
                + messageContent
                + "<br><br>"
                + "<small style='color: #666;'>You received this email because you are subscribed to CarComps promotions. "
                + "<a href='https://carcomps.hu/unsubscribe'>Unsubscribe</a></small>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=utf-8");

        // ⭐ BATCH SENDING - Send to multiple users in batches (max 50 per email) ⭐
        int batchSize = 50;
        int totalSent = 0;

        for (int i = 0; i < subscribedEmails.size(); i += batchSize) {
            try {
                // Get batch of emails
                int endIndex = Math.min(i + batchSize, subscribedEmails.size());
                List<String> batchEmails = subscribedEmails.subList(i, endIndex);

                // Create new message for this batch
                MimeMessage batchMessage = new MimeMessage(session);
                batchMessage.setFrom(new InternetAddress("Promotion@Carcomps.hu"));
                batchMessage.setSubject("Promotion from CarComps");
                batchMessage.setContent(htmlContent, "text/html; charset=utf-8");

                // Add all batch emails as BCC (recipients won't see each other)
                InternetAddress[] bccAddresses = new InternetAddress[batchEmails.size()];
                for (int j = 0; j < batchEmails.size(); j++) {
                    bccAddresses[j] = new InternetAddress(batchEmails.get(j));
                }
                batchMessage.setRecipients(MimeMessage.RecipientType.BCC, bccAddresses);

                // Send the batch
                Transport.send(batchMessage);

                totalSent += batchEmails.size();
                System.out.println("Batch " + ((i / batchSize) + 1) + " sent: " + batchEmails.size() + " emails");

                // Optional: Small delay between batches to avoid spam filters
                if (endIndex < subscribedEmails.size()) {
                    Thread.sleep(1000); // 1 second delay
                }

            } catch (Exception ex) {
                System.err.println("Error sending batch starting at index " + i + ": " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        System.out.println("✅ Promotion Email Campaign Complete! Total sent: " + totalSent);
    }
}
