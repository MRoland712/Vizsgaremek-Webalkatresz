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
import javax.mail.internet.MimeMultipart;
import com.mycompany.vizsgaremek.model.Users;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.mail.Store;
import javax.mail.Folder;
import com.mycompany.vizsgaremek.model.EmailInfo;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.internet.MimeBodyPart;

/**
 *
 * @author ddori
 */
public class SendEmail {

    /**
     * Sends an activation email to a newly registered user.
     *
     * The email contains an activation link with the user's registration token
     * as a query parameter. The link is valid for 24 hours and must be clicked
     * to activate the user account.
     *
     * Email is sent from the alias "noreply@carcomps.hu" using Gmail SMTP.
     *
     * @param recipientEmail The email address of the user to send the
     * activation link to
     * @param userdata The Users object containing user information (firstName,
     * lastName, registrationToken)
     * @throws MessagingException if email sending fails due to SMTP
     * configuration or network issues
     */
    public static void sendActivationEmail(String recipientEmail, Users userdata) throws MessagingException {

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

        // Set the "From" header
        message.setFrom(new InternetAddress("noreply@carcomps.hu"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("CarComps - Fiók aktiválás");

        // Get user data
        String firstName = userdata.getFirstName();
        String lastName = userdata.getLastName();
        String registrationToken = userdata.getRegistrationToken();

        // Activation link
        String activationLink = "https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/user/activateUser?activationToken=" + registrationToken;

        // Create HTML content
        String htmlContent = "<!DOCTYPE html>"
                + "<html lang=\"hu\">"
                + "<head>"
                + "<meta charset=\"UTF-8\" />"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />"
                + "<title>Fiók aktiválás</title>"
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
                + ".btn-container { display: flex; justify-content: center; margin: 30px 0; }"
                + ".btn { display: inline-block; padding: 14px 32px; background: #ff6600; color: #fff; text-decoration: none; border-radius: 6px; font-weight: 600; font-size: 16px; }"
                + ".btn:hover { background: #e55a00; }"
                + ".link-text { word-break: break-all; font-size: 14px; color: #9fc5ff; background: rgba(255,255,255,0.05); padding: 12px; border-radius: 4px; margin: 20px 0; font-family: monospace; }"
                + ".email-footer p { color: #bfbfbf; font-size: 12px; margin: 0; text-align: center; }"
                + ".email-footer a { color: #9fc5ff; text-decoration: none; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"email-container\">"
                + "<div class=\"email-header\">"
                + "<img src=\"https://raw.githubusercontent.com/MRoland712/Vizsgaremek-Webalkatresz/refs/heads/Backend/K%C3%A9pek/CarComps_Logo_BigassC.png\" alt=\"CarComps logó\" />"
                + "</div>"
                + "<div class=\"email-body\">"
                + "<hr />"
                + "<h2>Kedves " + lastName + " " + firstName + "!</h2>"
                + "<p>Köszönjük, hogy regisztrált a CarComps webshopunkban! Az alábbi gombra kattintva aktiválhatja fiókját:</p>"
                + "<div class=\"btn-container\">"
                + "<a href=\"" + activationLink + "\" class=\"btn\">Fiók aktiválása</a>"
                + "</div>"
                + "<p>Ha a gomb nem működik, másolja be az alábbi linket a böngészőjébe:</p>"
                + "<div class=\"link-text\">" + activationLink + "</div>"
                + "<p>Ez a link <span style=\"text-decoration: underline\">24 óráig</span> érvényes.</p>"
                + "<p>Ha Ön nem regisztrált a CarComps oldalán, kérjük, hagyja figyelmen kívül ezt az e-mailt.</p>"
                + "<p>Üdvözlettel,<br />CarComps csapata</p>"
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

        System.out.println("Activation email sent to: " + recipientEmail);
    }

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
                + "<img src=\"https://raw.githubusercontent.com/MRoland712/Vizsgaremek-Webalkatresz/refs/heads/Backend/K%C3%A9pek/CarComps_Logo_BigassC.png\" alt=\"CarComps logó\" />"
                + "</div>"
                + "<div class=\"email-body\">"
                + "<hr />"
                + "<h2>Kedves " + lastName + "!</h2>"
                + "<p>Az Ön fiókjának biztonsága érdekében kétlépcsős azonosítást használunk. Az alábbi egyszeri kódot kell megadnia a bejelentkezéshez:</p>"
                + "<div class=\"auth-container\">"
                + "<div class=\"code\" style=\"margin: auto;\"><span id=\"number\">" + OTP + "</span></div>"
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
            updateAuthUser.setRole(userdata.getRole().equals("user") ? "user" : "admin");
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
    public static void sendPromotionEmailToSubscribedUsers(String promotionImageLink, String bodyText, String code, String expirationDate) throws MessagingException {

        // Get all users
        ArrayList<Users> allUsers = Users.getUsers();

        if (allUsers == null || allUsers.isEmpty()) {
            System.out.println("No users found in database");
            return;
        }

        // Filter subscribed users
        ArrayList<String> subscribedEmails = new ArrayList<>();
        for (Users user : allUsers) {
            if (user.getIsSubscribed() != null && user.getIsSubscribed() == true && user.getEmail() != null) {
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
        message.setSubject("Carcomps Akció!");

        // Create HTML content
        String htmlContent = "<!DOCTYPE html>"
                + "<html lang=\"en\">"
                + "<head>"
                + "<meta charset=\"UTF-8\" />"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />"
                + "<title>Promociós Email</title>"
                + "<style>"
                + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                + "body { background: #111; padding: 20px; font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; }"
                + ".email-container { border-radius: 10px; max-width: 600px; width: 100%; margin: 20px auto; background-color: #2b2b2b; color: #fffafa; text-align: center; overflow: hidden; }"
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
                + ".promotion-pic { width: 100%; border-radius: 8px; margin: 20px 0; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"email-container\">"
                + "<div class=\"email-header\">"
                + "<img src=\"https://raw.githubusercontent.com/MRoland712/Vizsgaremek-Webalkatresz/refs/heads/Backend/K%C3%A9pek/CarComps_Logo_BigassC.png\" alt=\"CarComps logó\" />"
                + "</div>"
                + "<div class=\"email-body\">"
                + "<hr />"
                + "<img class=\"promotion-pic\" src=\"" + promotionImageLink + "\" alt=\"Promócióról kép\" />"
                + "<h2>Kedves Ügyfelünk!</h2>"
                + "<p>" + bodyText + "</p>"
                + "<div class=\"auth-container\">"
                + "<div class=\"code\"><span id=\"number\">" + code + "</span></div>"
                + "</div>"
                + "<p>Lejárati dátum: " + expirationDate + "</p>"
                + "</div>"
                + "<hr />"
                + "<div class=\"email-footer\">"
                + "<p>Ez egy automatikus üzenet, kérjük, ne válaszolj rá.<br />"
                + "© 2025 CarComps – Minden jog fenntartva.<br />"
                + "Székhely: 7621 Pécs, Fő utca 12.<br />"
                + "<a href=\"https://carcomps.hu/adatvedelem\">Adatvédelmi nyilatkozat</a> | "
                + "<a href=\"https://carcomps.hu/aszf\">Felhasználási feltételek</a> | "
                + "<a href=\"https://api.carcomps.hu/webresources/user/leiratkozas\">Leiratkozás a hírlevélről</a>"
                + "</p>"
                + "</div>"
                + "</div>"
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
                batchMessage.setFrom(new InternetAddress("promotion@carcomps.hu"));
                batchMessage.setSubject("Carcomps Akció!");
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

        System.out.println("Promotion Email Campaign Complete! Total sent: " + totalSent);
    }

    /**
     * Send an email to a customer with alias of "ugyfelszolgalat@carcomps.hu"
     *
     * @param recipientEmail Customer's email / who to send the email to
     * @param emailSubject Subject of the email
     * @param customerName Customer's name
     * @param emailMessage Message body
     * @param inReplyToMessageId (Optional) Original message ID to reply to
     * @throws MessagingException if email sending fails
     */
    public static void sendCustomerSupportEmail(
            String recipientEmail,
            String emailSubject,
            String customerName,
            String emailMessage,
            String inReplyToMessageId
    ) throws MessagingException {

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

        // Set the "From" header to your alias
        message.setFrom(new InternetAddress("ugyfelszolgalat@carcomps.hu"));

        // Set recipient
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

        // ⭐ Set Reply-To header (így a válasz ide megy) ⭐
        message.setReplyTo(InternetAddress.parse("ugyfelszolgalat@carcomps.hu"));

        // ⭐ Ha egy korábbi emailre válaszolunk ⭐
        if (inReplyToMessageId != null && !inReplyToMessageId.isEmpty()) {
            message.setHeader("In-Reply-To", inReplyToMessageId);
            message.setHeader("References", inReplyToMessageId);
            message.setSubject("Re: " + emailSubject);  // "Re:" prefix
        } else {
            message.setSubject(emailSubject);
        }

        // Create HTML content
        String htmlContent = "<!DOCTYPE html>"
                + "<html lang=\"en\">"
                + "<head>"
                + "<meta charset=\"UTF-8\" />"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />"
                + "<title>Ügyfélszolgálat Email</title>"
                + "<style>"
                + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                + "body { background: #111; padding: 20px; font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; }"
                + ".email-container { border-radius: 10px; max-width: 600px; width: 100%; margin: 20px auto; background-color: #2b2b2b; color: #FFFAFA; text-align: center; overflow: hidden; }"
                + ".email-header, .email-footer { padding: 16px; }"
                + ".email-header img { width: 300px; display: block; margin: auto; }"
                + ".email-body { padding: 0 24px 24px; text-align: left; }"
                + "hr { border: none; height: 1px; background: rgba(255, 255, 255, 0.06); margin: 20px 0; }"
                + "h2 { margin: 18px 0; color: #fffafa; font-size: 20px; text-align: left; }"
                + "p { margin: 0 0 16px; font-size: 16px; line-height: 1.6; color: #eaeaea; }"
                + ".email-footer p { color: #bfbfbf; font-size: 12px; margin: 0; text-align: center; }"
                + ".email-footer a { color: #9fc5ff; text-decoration: none; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"email-container\">"
                + "<div class=\"email-header\">"
                + "<img src=\"https://raw.githubusercontent.com/MRoland712/Vizsgaremek-Webalkatresz/refs/heads/Backend/K%C3%A9pek/CarComps_Logo_BigassC.png\" alt=\"CarComps logó\" />"
                + "</div>"
                + "<div class=\"email-body\">"
                + "<hr />"
                + "<h2>Kedves " + customerName + "!</h2>"
                + "<p>" + emailMessage + "</p>"
                + "<p>Üdvözlettel: CarComps csapata</p>"
                + "</div>"
                + "<hr />"
                + "<div class=\"email-footer\">"
                + "<p>Erre az emailra küldtük: " + recipientEmail + "</p>"
                + "<p>© 2025 CarComps – Minden jog fenntartva.<br />"
                + "Székhely: 7621 Pécs, Fő utca 12.<br />"
                + "<a href=\"https://carcomps.hu/adatvedelem\">Adatvédelmi nyilatkozat</a> | "
                + "<a href=\"https://carcomps.hu/aszf\">Felhasználási feltételek</a>"
                + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=utf-8");

        // Send the email
        Transport.send(message);

        System.out.println("Email Sent to: " + recipientEmail);
    }

    public static List<EmailInfo> getMessages() throws MessagingException {
        // IMAP properties
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.host", "imap.gmail.com");
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");

        // Connect to mailbox
        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect(
                "imap.gmail.com",
                KvFetcher.getDataFromKV("SMTPEmail"),
                base64Converters.base64Converter(KvFetcher.getDataFromKV("SMTPPsw"))
        );

        // Open INBOX
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        // Get messages
        Message[] messages = inbox.getMessages();

        List<EmailInfo> emailList = new ArrayList<>();

        // ⭐ Végigmegyünk MINDEN message-en ⭐
        if (messages.length > 0) {
            for (int i = 0; i < messages.length; i++) {
                try {
                    Message message = messages[i];

                    // ⭐ Message-ID kinyerése ⭐
                    String[] messageIdHeaders = message.getHeader("Message-ID");
                    String messageId = (messageIdHeaders != null && messageIdHeaders.length > 0)
                            ? messageIdHeaders[0]
                            : null;

                    // From
                    String from = (message.getFrom() != null && message.getFrom().length > 0)
                            ? message.getFrom()[0].toString()
                            : "Unknown";

                    // Subject
                    String subject = message.getSubject() != null
                            ? message.getSubject()
                            : "(No Subject)";

                    // Date
                    Date receivedDate = message.getReceivedDate();

                    // Body content
                    String bodyContent = getTextFromMessage(message);

                    // ⭐ EmailInfo objektum létrehozása ⭐
                    EmailInfo emailInfo = new EmailInfo();
                    emailInfo.setMessageId(messageId);
                    emailInfo.setFrom(from);
                    emailInfo.setSubject(subject);
                    emailInfo.setReceivedDate(receivedDate);
                    emailInfo.setBodyContent(bodyContent);

                    emailList.add(emailInfo);

                } catch (Exception ex) {
                    System.err.println("Error processing message #" + (i + 1) + ": " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } else {
            System.out.println("No messages found in INBOX");
        }

        inbox.close(false);
        store.close();

        return emailList;
    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("text/html")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append(html);
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    /**
     * Archive email by Message-ID (moves to "All Mail" folder, removes from
     * Inbox)
     *
     * @param messageId The Message-ID header of the email to archive
     * @return true if successful, false otherwise
     */
    /**
     * Archive email by Message-ID (moves to "All Mail" folder, removes from
     * Inbox)
     *
     * @param messageId The Message-ID header of the email to archive
     * @return true if successful, false otherwise
     */
    public static boolean archiveEmailByMessageId(String messageId) {
        if (messageId == null || messageId.isBlank()) {
            return false;
        }

        System.out.println("Searching for Message-ID: " + messageId);

        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imap.host", "imap.gmail.com");
            props.put("mail.imap.port", "993");
            props.put("mail.imap.ssl.enable", "true");

            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(
                    "imap.gmail.com",
                    KvFetcher.getDataFromKV("SMTPEmail"),
                    base64Converters.base64Converter(KvFetcher.getDataFromKV("SMTPPsw"))
            );

            // 🔹 INBOX-ban keresünk
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.getMessages();
            Message target = null;

            System.out.println("Searching through " + messages.length + " messages in INBOX...");

            for (Message msg : messages) {
                String[] headers = msg.getHeader("Message-ID");
                if (headers != null && headers.length > 0) {
                    String msgId = headers[0].trim();

                    // DEBUG
                    System.out.println("   Comparing: [" + msgId + "] vs [" + messageId + "]");

                    if (msgId.equals(messageId)) {
                        target = msg;
                        System.out.println("FOUND!");
                        break;
                    }
                }
            }

            if (target == null) {
                System.out.println("No message found with Message-ID: " + messageId);
                inbox.close(false);
                store.close();
                return false;
            }

            // ⭐ Gmail archiválás = DELETED flag + expunge ⭐
            // Ez eltávolítja az INBOX-ból, de megmarad az "All Mail"-ben
            target.setFlag(Flags.Flag.DELETED, true);

            System.out.println("Email archived successfully!");
            System.out.println("   Message-ID: " + messageId);
            System.out.println("   Subject: " + target.getSubject());
            System.out.println("   From: " + (target.getFrom() != null && target.getFrom().length > 0
                    ? target.getFrom()[0] : "Unknown"));

            inbox.close(true);  // ⭐ true = expunge (apply DELETED flag)
            store.close();

            return true;

        } catch (Exception ex) {
            System.err.println("Error archiving email: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    //PDF generatálás kiküldés 
    // -------------------------------------------------------------------------
    /**
     * Send payment confirmation email to customer
     *
     * @param recipientEmail Customer's email address
     * @param orderId Order ID
     * @param amount Payment amount
     * @param method Payment method
     * @param invoiceUrl Link to invoice PDF
     * @param paymentDate Payment date
     * @throws MessagingException if email sending fails
     */
    /**
     * Send payment confirmation email with PDF attachment
     */
    public static void sendPaymentConfirmationEmail(
            String recipientEmail,
            Integer orderId,
            BigDecimal amount,
            String method,
            String invoiceUrl,
            Date paymentDate,
            byte[] pdfAttachment) throws MessagingException {  // PDF csatolmány hozzáadva

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
        message.setFrom(new InternetAddress("fizetesek@carcomps.hu"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Fizetés megerősítve - Rendelés #" + orderId);

        // Format date and amount
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(paymentDate);
        String formattedAmount = String.format("%,.2f Ft", amount);

        // Create HTML content (UGYANAZ marad)
        String htmlContent = "<!DOCTYPE html>"
                + "<html lang=\"en\">"
                + "<head>"
                + "<meta charset=\"UTF-8\" />"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />"
                + "<title>Fizetés megerősítés</title>"
                + "<style>"
                + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                + "body { background: #111; padding: 20px; font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; }"
                + ".email-container { border-radius: 10px; max-width: 600px; width: 100%; margin: 20px auto; background-color: #2b2b2b; color: #fffafa; text-align: center; overflow: hidden; }"
                + ".email-header, .email-footer { padding: 16px; }"
                + ".email-header img { width: 300px; display: block; margin: auto; }"
                + ".email-body { padding: 0 24px 24px; text-align: left; }"
                + "hr { border: none; height: 1px; background: rgba(255, 255, 255, 0.06); margin: 20px 0; }"
                + "h2 { margin: 18px 0; color: #fffafa; font-size: 20px; text-align: left; }"
                + "p { margin: 0 0 16px; font-size: 16px; line-height: 1.6; color: #eaeaea; }"
                + ".details { background-color: #1a1a1a; padding: 15px; border-left: 4px solid #ff6600; margin: 20px 0; border-radius: 4px; }"
                + ".details p { margin: 8px 0; }"
                + ".button { display: inline-block; background-color: #ff6600; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin-top: 20px; }"
                + ".email-footer p { color: #bfbfbf; font-size: 12px; margin: 0; text-align: center; }"
                + ".email-footer a { color: #9fc5ff; text-decoration: none; }"
                + ".success-icon { font-size: 48px; text-align: center; margin: 10px 0; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"email-container\">"
                + "<div class=\"email-header\">"
                + "<img src=\"https://raw.githubusercontent.com/MRoland712/Vizsgaremek-Webalkatresz/refs/heads/Backend/K%C3%A9pek/CarComps_Logo_BigassC.png\" alt=\"CarComps logó\" />"
                + "</div>"
                + "<div class=\"email-body\">"
                + "<hr />"
                + "<div class=\"success-icon\"></div>"
                + "<h2>Fizetés sikeres!</h2>"
                + "<p>Kedves Vásárló!</p>"
                + "<p>Sikeresen feldolgoztuk a fizetését. Köszönjük a vásárlást!</p>"
                + "<div class=\"details\">"
                + "<h3 style=\"color: #fffafa; margin-bottom: 12px;\">Fizetési részletek:</h3>"
                + "<p><strong>Rendelés szám:</strong> #" + orderId + "</p>"
                + "<p><strong>Összeg:</strong> " + formattedAmount + "</p>"
                + "<p><strong>Fizetési mód:</strong> " + getPaymentMethod(method) + "</p>"
                + "<p><strong>Fizetés dátuma:</strong> " + formattedDate + "</p>"
                + "</div>"
                + "<p style=\"margin-top: 20px;\">Ha bármilyen kérdése van, kérjük vegye fel velünk a kapcsolatot.</p>"
                + "<p>Üdvözlettel: CarComps csapata</p>"
                + "</div>"
                + "<hr />"
                + "<div class=\"email-footer\">"
                + "<p>Ez egy automatikus üzenet, kérjük, ne válaszolj rá.<br />"
                + "© 2025 CarComps – Minden jog fenntartva.<br />"
                + "Székhely: 7621 Pécs, Fő utca 12.<br />"
                + "<a href=\"https://carcomps.hu/adatvedelem\">Adatvédelmi nyilatkozat</a> | "
                + "<a href=\"https://carcomps.hu/aszf\">Felhasználási feltételek</a>"
                + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        // PDF CSATOLÁS 
        if (pdfAttachment != null && pdfAttachment.length > 0) {
            try {
                // Multipart message létrehozása
                MimeMultipart multipart = new MimeMultipart();

                // HTML rész
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
                multipart.addBodyPart(htmlPart);

                // PDF csatolmány
                MimeBodyPart attachmentPart = new MimeBodyPart();
                javax.activation.DataSource source = new javax.mail.util.ByteArrayDataSource(pdfAttachment, "application/pdf");
                attachmentPart.setDataHandler(new javax.activation.DataHandler(source));
                attachmentPart.setFileName("szamla_" + orderId + ".pdf");
                multipart.addBodyPart(attachmentPart);

                // Set multipart content
                message.setContent(multipart);

                System.out.println("PDF csatolmány hozzáadva az emailhez (" + pdfAttachment.length + " bytes)");

            } catch (Exception ex) {
                System.err.println("PDF csatolás hiba, email HTML-ként megy: " + ex.getMessage());
                // Fallback: csak HTML email küldése
                message.setContent(htmlContent, "text/html; charset=utf-8");
            }
        } else {
            // Nincs PDF csatolmány, csak HTML email
            message.setContent(htmlContent, "text/html; charset=utf-8");
        }

        // Send the email
        Transport.send(message);

        System.out.println("Payment confirmation email sent to: " + recipientEmail);
    }

    private static String getPaymentMethod(String method) {
        switch (method) {
            case "credit_card":
                return "Bankkártya";
            case "debit_card":
                return "Betéti kártya";
            case "paypal":
                return "PayPal";
            case "cash_on_delivery":
                return "Utánvét";
            case "bank_transfer":
                return "Banki átutalás";
            default:
                return method;
        }
    }

    public static void sendPasswordResetEmail(String recipientEmail, String username, String token) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        KvFetcher.getDataFromKV("SMTPEmail"),
                        base64Converters.base64Converter(KvFetcher.getDataFromKV("SMTPPsw"))
                );
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply@carcomps.hu"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("CarComps - Jelszó visszaállítás");

        String resetLink = "https://api.carcomps.hu/passwordReset/resetPasswordPage?token=" + token;

        String htmlContent = "<!DOCTYPE html>"
                + "<html lang=\"hu\">"
                + "<head>"
                + "<meta charset=\"UTF-8\" />"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />"
                + "<title>Jelszó visszaállítás</title>"
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
                + ".btn-container { display: flex; justify-content: center; margin: 30px 0; }"
                + ".btn { display: inline-block; padding: 14px 32px; background: #ff6600; color: #fff; text-decoration: none; border-radius: 6px; font-weight: 600; font-size: 16px; }"
                + ".link-text { word-break: break-all; font-size: 14px; color: #9fc5ff; background: rgba(255,255,255,0.05); padding: 12px; border-radius: 4px; margin: 20px 0; font-family: monospace; }"
                + ".email-footer p { color: #bfbfbf; font-size: 12px; margin: 0; text-align: center; }"
                + ".email-footer a { color: #9fc5ff; text-decoration: none; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"email-container\">"
                + "<div class=\"email-header\">"
                + "<img src=\"https://raw.githubusercontent.com/MRoland712/Vizsgaremek-Webalkatresz/refs/heads/Backend/K%C3%A9pek/CarComps_Logo_BigassC.png\" alt=\"CarComps logó\" />"
                + "</div>"
                + "<div class=\"email-body\">"
                + "<hr />"
                + "<h2>Kedves " + username + "!</h2>"
                + "<p>Jelszó visszaállítási kérelmet kaptunk a fiókodhoz. Az alábbi gombra kattintva állíthatod vissza a jelszavadat:</p>"
                + "<div class=\"btn-container\">"
                + "<a href=\"" + resetLink + "\" class=\"btn\">Jelszó visszaállítása</a>"
                + "</div>"
                + "<p>Ha a gomb nem működik, másold be az alábbi linket a böngészőbe:</p>"
                + "<div class=\"link-text\">" + resetLink + "</div>"
                + "<p>Ez a link <span style=\"text-decoration: underline\">1 óráig</span> érvényes.</p>"
                + "<p>Ha nem te kérted a jelszó visszaállítást, hagyd figyelmen kívül ezt az emailt.</p>"
                + "<p>Üdvözlettel,<br />CarComps csapata</p>"
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
        Transport.send(message);

        System.out.println("Password reset email sent to: " + recipientEmail);
    }

    public static void sendEmailVerificationEmail(String recipientEmail, String username, String token) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        KvFetcher.getDataFromKV("SMTPEmail"),
                        base64Converters.base64Converter(KvFetcher.getDataFromKV("SMTPPsw"))
                );
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply@carcomps.hu"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("CarComps - Email cím megerősítése");

        String verificationLink = "https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/EmailVerifications/activateEmail?activationToken=" + token;

        String htmlContent = "<!DOCTYPE html>"
                + "<html lang=\"hu\">"
                + "<head>"
                + "<meta charset=\"UTF-8\" />"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />"
                + "<title>Email cím megerősítése</title>"
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
                + ".btn-container { display: flex; justify-content: center; margin: 30px 0; }"
                + ".btn { display: inline-block; padding: 14px 32px; background: #ff6600; color: #fff; text-decoration: none; border-radius: 6px; font-weight: 600; font-size: 16px; }"
                + ".link-text { word-break: break-all; font-size: 14px; color: #9fc5ff; background: rgba(255,255,255,0.05); padding: 12px; border-radius: 4px; margin: 20px 0; font-family: monospace; }"
                + ".email-footer p { color: #bfbfbf; font-size: 12px; margin: 0; text-align: center; }"
                + ".email-footer a { color: #9fc5ff; text-decoration: none; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"email-container\">"
                + "<div class=\"email-header\">"
                + "<img src=\"https://raw.githubusercontent.com/MRoland712/Vizsgaremek-Webalkatresz/refs/heads/Backend/K%C3%A9pek/CarComps_Logo_BigassC.png\" alt=\"CarComps logó\" />"
                + "</div>"
                + "<div class=\"email-body\">"
                + "<hr />"
                + "<h2>Kedves " + username + "!</h2>"
                + "<p>Köszönjük, hogy regisztráltál a CarComps webshopban! Az alábbi gombra kattintva erősítsd meg az email címedet:</p>"
                + "<div class=\"btn-container\">"
                + "<a href=\"" + verificationLink + "\" class=\"btn\">Email cím megerősítése</a>"
                + "</div>"
                + "<p>Ha a gomb nem működik, másold be az alábbi linket a böngészőbe:</p>"
                + "<div class=\"link-text\">" + verificationLink + "</div>"
                + "<p>Ez a link <span style=\"text-decoration: underline\">24 óráig</span> érvényes.</p>"
                + "<p>Ha nem te regisztráltál a CarComps oldalán, hagyd figyelmen kívül ezt az emailt.</p>"
                + "<p>Üdvözlettel,<br />CarComps csapata</p>"
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
        Transport.send(message);

        System.out.println("Email verification email sent to: " + recipientEmail);
    }
}
