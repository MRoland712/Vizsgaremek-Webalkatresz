package com.mycompany.vizsgaremek.model;

import java.util.Date;

public class EmailInfo {
    private String messageId;
    private String from;
    private String subject;
    private Date receivedDate;
    private String bodyContent;
    
    // Constructors
    public EmailInfo() {}
    
    public EmailInfo(String messageId, String from, String subject, Date receivedDate, String bodyContent) {
        this.messageId = messageId;
        this.from = from;
        this.subject = subject;
        this.receivedDate = receivedDate;
        this.bodyContent = bodyContent;
    }
    
    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public Date getReceivedDate() {
        return receivedDate;
    }
    
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
    
    public String getBodyContent() {
        return bodyContent;
    }
    
    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }
    
    @Override
    public String toString() {
        return "EmailInfo{" +
                "messageId='" + messageId + '\'' +
                ", from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", receivedDate=" + receivedDate +
                ", bodyContent='" + (bodyContent.length() > 50 ? bodyContent.substring(0, 50) + "..." : bodyContent) + '\'' +
                '}';
    }
}