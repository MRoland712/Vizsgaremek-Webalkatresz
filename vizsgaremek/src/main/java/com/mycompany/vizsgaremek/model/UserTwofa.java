/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import com.mycompany.vizsgaremek.service.AuthenticationService;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author neblgergo
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
    @NamedQuery(name = "UserTwofa.findByUpdatedAt", query = "SELECT u FROM UserTwofa u WHERE u.updatedAt = :updatedAt"),
    @NamedQuery(name = "UserTwofa.findByIsDeleted", query = "SELECT u FROM UserTwofa u WHERE u.isDeleted = :isDeleted"),
    @NamedQuery(name = "UserTwofa.findByDeletedAt", query = "SELECT u FROM UserTwofa u WHERE u.deletedAt = :deletedAt")})
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
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static AuthenticationService.userTwofaAuth userTwofaAuth = new AuthenticationService.userTwofaAuth();

    public UserTwofa(Integer id, Boolean twofaEnabled, String twofaSecret, String recoveryCodes, Date createdAt, Date updatedAt, Boolean isDeleted, Date deletedAt, Users userId) {
        this.id = id;
        this.twofaEnabled = twofaEnabled;
        this.twofaSecret = twofaSecret;
        this.recoveryCodes = recoveryCodes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.userId = userId;
    }


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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
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
    
    public static Boolean updateUserTwofa(UserTwofa updatedUserTwofa) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createUserTwoFa");

            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("user_IdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("twofa_enabledIN", Boolean.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("twofa_secretIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("recovery_codesIN", String.class, ParameterMode.IN);

            spq.setParameter("idIN", updatedUserTwofa.getId());
            spq.setParameter("user_IdIN", updatedUserTwofa.getUserId().getId());
            spq.setParameter("twofa_enabledIN", updatedUserTwofa.getTwofaEnabled());
            spq.setParameter("twofa_secretIN", updatedUserTwofa.getTwofaSecret());
            spq.setParameter("recovery_codesIN", updatedUserTwofa.getRecoveryCodes());

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

    public static UserTwofa getUserTwofaByUserId(Integer userId) {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getUserTwoFaByUserId");
            spq.registerStoredProcedureParameter("user_idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("user_idIN", userId);

            spq.execute();

            List<Object[]> resultList = spq.getResultList();
            
            if (resultList == null || resultList.isEmpty()) {
                return null;
            }
            
            //Integer id, Boolean twofaEnabled, String twofaSecret, String recoveryCodes, Date createdAt, 
            //Date updatedAt, Boolean isDeleted, Date deletedAt
            UserTwofa toReturn = new UserTwofa();
            for (Object[] record : resultList) {
                
                Users users = new Users();
                users.setId(Integer.valueOf(record[1].toString())); //userId

                UserTwofa ufa = new UserTwofa(
                        Integer.valueOf(record[0].toString()),// id
                        Boolean.valueOf(record[2].toString()),// twofaEnabled
                        record[3].toString(),// twofaSecret
                        record[4].toString(),// recoveryCodes
                        record[5] == null ? null : formatter.parse(record[5].toString()), // createdAt
                        record[6] == null ? null : formatter.parse(record[6].toString()),// updatedAt
                        Boolean.valueOf(record[7].toString()),// isDeleted
                        record[8] == null ? null : formatter.parse(record[8].toString()),// deletedAt
                        users
                );
                toReturn = ufa;
            }
            return toReturn;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Ha hiba van, rollback
            }
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
}
