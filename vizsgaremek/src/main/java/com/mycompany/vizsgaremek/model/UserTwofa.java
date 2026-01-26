/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import com.mycompany.vizsgaremek.service.AuthenticationService;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.mycompany.vizsgaremek.service.AuthenticationService;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;

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
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //public AuthenticationService.userTwofaAuth userTwofaAuth = new AuthenticationService.userTwofaAuth();
    
    public UserTwofa() {
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
    
    //userId, 0, secretKey, recoveryCodes.toString()
    public static Boolean createUserTwofa(Integer userId, Boolean isEnabled, String secretKey, String recoveryCodes) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createUserTwoFa");

            spq.registerStoredProcedureParameter("user_idIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("twofa_enabledIN", Boolean.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("twofa_secretIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("recovery_codesIN", String.class, ParameterMode.IN);

            spq.setParameter("user_idIN", userId);
            spq.setParameter("twofa_enabledIN", isEnabled);
            spq.setParameter("twofa_secretIN", secretKey);
            spq.setParameter("recovery_codesIN", recoveryCodes);

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }
}
