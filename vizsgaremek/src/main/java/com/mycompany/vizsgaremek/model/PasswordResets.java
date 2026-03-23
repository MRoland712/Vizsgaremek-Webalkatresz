/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
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
@Table(name = "password_resets")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PasswordResets.findAll", query = "SELECT p FROM PasswordResets p"),
    @NamedQuery(name = "PasswordResets.findById", query = "SELECT p FROM PasswordResets p WHERE p.id = :id"),
    @NamedQuery(name = "PasswordResets.findByToken", query = "SELECT p FROM PasswordResets p WHERE p.token = :token"),
    @NamedQuery(name = "PasswordResets.findByExpiresAt", query = "SELECT p FROM PasswordResets p WHERE p.expiresAt = :expiresAt"),
    @NamedQuery(name = "PasswordResets.findByUsed", query = "SELECT p FROM PasswordResets p WHERE p.used = :used"),
    @NamedQuery(name = "PasswordResets.findByCreatedAt", query = "SELECT p FROM PasswordResets p WHERE p.createdAt = :createdAt")})
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

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    //getByToken
    public PasswordResets(Integer id, String token, Date expiresAt, Boolean used, Date createdAt, Users userId) {
        this.id = id;
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = used;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    //create, update
    public PasswordResets(String token) {
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

    // createPasswordReset
    public static Boolean createPasswordReset(Integer userId, String token) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createPasswordReset");
            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("tokenIN", String.class, ParameterMode.IN);
            spq.setParameter("userIdIN", userId);
            spq.setParameter("tokenIN", token);
            spq.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // getPasswordResetByToken
    public static PasswordResets getPasswordResetByToken(String token) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPasswordResetByToken");
            spq.registerStoredProcedureParameter("tokenIN", String.class, ParameterMode.IN);
            spq.setParameter("tokenIN", token);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            PasswordResets toReturn = new PasswordResets();

            for (Object[] record : resultList) {
                // userId Users objektum létrehozása
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                PasswordResets pr = new PasswordResets(
                        Integer.valueOf(record[0].toString()), // id
                        record[2] != null ? record[2].toString() : null, // token
                        record[3] == null ? null : formatter.parse(record[3].toString()), // expires_at
                        Boolean.FALSE, // used
                        record[5] == null ? null : formatter.parse(record[5].toString()), // created_at
                        user // userId
                );
                toReturn = pr;
            }
            return toReturn;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }

    // updatePasswordReset used = 1
    public static Boolean updatePasswordReset(String token) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updatePasswordReset");
            spq.registerStoredProcedureParameter("tokenIN", String.class, ParameterMode.IN);
            spq.setParameter("tokenIN", token);
            spq.execute();
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

    // softDeletePasswordReset
    public static Boolean softDeletePasswordReset(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeletePasswordReset");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

    public static Integer resetPassword(String token, String encryptedPassword) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("resetPassword");
            spq.registerStoredProcedureParameter("tokenIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("newPasswordIN", String.class, ParameterMode.IN);
            spq.setParameter("tokenIN", token);
            spq.setParameter("newPasswordIN", encryptedPassword);
            spq.execute();

            List<Object> resultList = spq.getResultList();
            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            Object record = resultList.get(0);
            return record != null ? Integer.valueOf(record.toString()) : null;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }

}
