/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author neblgergo
 */
@Entity
@Table(name = "email_verifications")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EmailVerifications.findAll", query = "SELECT e FROM EmailVerifications e"),
    @NamedQuery(name = "EmailVerifications.findById", query = "SELECT e FROM EmailVerifications e WHERE e.id = :id"),
    @NamedQuery(name = "EmailVerifications.findByToken", query = "SELECT e FROM EmailVerifications e WHERE e.token = :token"),
    @NamedQuery(name = "EmailVerifications.findByVerified", query = "SELECT e FROM EmailVerifications e WHERE e.verified = :verified"),
    @NamedQuery(name = "EmailVerifications.findBySentAt", query = "SELECT e FROM EmailVerifications e WHERE e.sentAt = :sentAt"),
    @NamedQuery(name = "EmailVerifications.findByVerifiedAt", query = "SELECT e FROM EmailVerifications e WHERE e.verifiedAt = :verifiedAt")})
public class EmailVerifications implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "token")
    private String token;
    @Column(name = "verified")
    private Boolean verified;
    @Column(name = "sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;
    @Column(name = "verified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date verifiedAt;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;

    public EmailVerifications() {
    }

    public EmailVerifications(Integer id) {
        this.id = id;
    }

    public EmailVerifications(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public Date getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Date verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmailVerifications)) {
            return false;
        }
        EmailVerifications other = (EmailVerifications) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.EmailVerifications[ id=" + id + " ]";
    }
    
}
