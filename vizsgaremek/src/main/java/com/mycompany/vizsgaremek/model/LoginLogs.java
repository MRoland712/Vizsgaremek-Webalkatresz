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
@Table(name = "login_logs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LoginLogs.findAll", query = "SELECT l FROM LoginLogs l"),
    @NamedQuery(name = "LoginLogs.findById", query = "SELECT l FROM LoginLogs l WHERE l.id = :id"),
    @NamedQuery(name = "LoginLogs.findByUserAgent", query = "SELECT l FROM LoginLogs l WHERE l.userAgent = :userAgent"),
    @NamedQuery(name = "LoginLogs.findByLoggedInAt", query = "SELECT l FROM LoginLogs l WHERE l.loggedInAt = :loggedInAt")})
public class LoginLogs implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "logged_in_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date loggedInAt;

    public LoginLogs() {
    }

    public LoginLogs(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Date getLoggedInAt() {
        return loggedInAt;
    }

    public void setLoggedInAt(Date loggedInAt) {
        this.loggedInAt = loggedInAt;
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
        if (!(object instanceof LoginLogs)) {
            return false;
        }
        LoginLogs other = (LoginLogs) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.LoginLogs[ id=" + id + " ]";
    }
    
}
