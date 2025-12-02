/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import static com.mycompany.vizsgaremek.model.Users.emf;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ddori
 */
@Entity
@Table(name = "user_logs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserLogs.findAll", query = "SELECT u FROM UserLogs u"),
    @NamedQuery(name = "UserLogs.findById", query = "SELECT u FROM UserLogs u WHERE u.id = :id"),
    @NamedQuery(name = "UserLogs.findByAction", query = "SELECT u FROM UserLogs u WHERE u.action = :action"),
    @NamedQuery(name = "UserLogs.findByCreatedAt", query = "SELECT u FROM UserLogs u WHERE u.createdAt = :createdAt")})
public class UserLogs implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "action")
    private String action;
    @Lob
    @Size(max = 65535)
    @Column(name = "details")
    private String details;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public UserLogs() {
    }

    public UserLogs(Integer id) {
        this.id = id;
    }

    public UserLogs(Integer id, String action, Date createdAt) {
        this.id = id;
        this.action = action;
        this.createdAt = createdAt;
    }
    //createUserLogs
    public UserLogs(String action, String details) {
        this.action = action;
        this.details = details;
    }
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
        if (!(object instanceof UserLogs)) {
            return false;
        }
        UserLogs other = (UserLogs) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.UserLogs[ id=" + id + " ]";
    }
    
    public static Boolean createUserLogs(UserLogs createdUserLog, Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createUserLogs");

            spq.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_action", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_details", String.class, ParameterMode.IN);

            spq.setParameter("p_user_id", userId);
            spq.setParameter("p_action", createdUserLog.getAction());
            spq.setParameter("p_details", createdUserLog.getDetails());

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
