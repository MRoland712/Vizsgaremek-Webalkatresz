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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ddori
 */
@Entity
@Table(name = "user_twofa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserTwofa.findAll", query = "SELECT u FROM UserTwofa u"),
    @NamedQuery(name = "UserTwofa.findById", query = "SELECT u FROM UserTwofa u WHERE u.id = :id"),
    @NamedQuery(name = "UserTwofa.findByTwofaEnabled", query = "SELECT u FROM UserTwofa u WHERE u.twofaEnabled = :twofaEnabled"),
    @NamedQuery(name = "UserTwofa.findByTwofaSecret", query = "SELECT u FROM UserTwofa u WHERE u.twofaSecret = :twofaSecret"),
    @NamedQuery(name = "UserTwofa.findByRecoveryCodes", query = "SELECT u FROM UserTwofa u WHERE u.recoveryCodes = :recoveryCodes"),
    @NamedQuery(name = "UserTwofa.findByCreatedAt", query = "SELECT u FROM UserTwofa u WHERE u.createdAt = :createdAt"),
    @NamedQuery(name = "UserTwofa.findByUpdatedAt", query = "SELECT u FROM UserTwofa u WHERE u.updatedAt = :updatedAt")})
public class UserTwofa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "twofa_enabled")
    private Boolean twofaEnabled;
    @Size(max = 255)
    @Column(name = "twofa_secret")
    private String twofaSecret;
    @Size(max = 1024)
    @Column(name = "recovery_codes")
    private String recoveryCodes;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public UserTwofa() {
    }

    public UserTwofa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getTwofaEnabled() {
        return twofaEnabled;
    }

    public void setTwofaEnabled(Boolean twofaEnabled) {
        this.twofaEnabled = twofaEnabled;
    }

    public String getTwofaSecret() {
        return twofaSecret;
    }

    public void setTwofaSecret(String twofaSecret) {
        this.twofaSecret = twofaSecret;
    }

    public String getRecoveryCodes() {
        return recoveryCodes;
    }

    public void setRecoveryCodes(String recoveryCodes) {
        this.recoveryCodes = recoveryCodes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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
        if (!(object instanceof UserTwofa)) {
            return false;
        }
        UserTwofa other = (UserTwofa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.UserTwofa[ id=" + id + " ]";
    }
    
}
