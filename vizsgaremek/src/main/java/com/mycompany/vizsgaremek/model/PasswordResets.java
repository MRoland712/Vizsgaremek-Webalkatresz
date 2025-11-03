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
 * @author neblg
 */
@Entity
@Table(name = "password_resets")
@NamedQueries({
    @NamedQuery(name = "PasswordResets.findAll", query = "SELECT p FROM PasswordResets p"),
    @NamedQuery(name = "PasswordResets.findById", query = "SELECT p FROM PasswordResets p WHERE p.id = :id"),
    @NamedQuery(name = "PasswordResets.findByToken", query = "SELECT p FROM PasswordResets p WHERE p.token = :token"),
    @NamedQuery(name = "PasswordResets.findByExpiresAt", query = "SELECT p FROM PasswordResets p WHERE p.expiresAt = :expiresAt"),
    @NamedQuery(name = "PasswordResets.findByUsed", query = "SELECT p FROM PasswordResets p WHERE p.used = :used"),
    @NamedQuery(name = "PasswordResets.findByCreatedAt", query = "SELECT p FROM PasswordResets p WHERE p.createdAt = :createdAt")})
@XmlRootElement
public class PasswordResets implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "token")
    private String token;
    @Basic(optional = false)
    @NotNull
    @Column(name = "expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;
    @Column(name = "used")
    private Boolean used;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;

    public PasswordResets() {
    }

    public PasswordResets(Integer id) {
        this.id = id;
    }

    public PasswordResets(Integer id, String token, Date expiresAt) {
        this.id = id;
        this.token = token;
        this.expiresAt = expiresAt;
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

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
        if (!(object instanceof PasswordResets)) {
            return false;
        }
        PasswordResets other = (PasswordResets) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.PasswordResets[ id=" + id + " ]";
    }
    
}
