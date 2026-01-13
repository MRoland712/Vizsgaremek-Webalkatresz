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
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.search.SearchTerm;
import javax.mail.search.MessageIDTerm;

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
}
