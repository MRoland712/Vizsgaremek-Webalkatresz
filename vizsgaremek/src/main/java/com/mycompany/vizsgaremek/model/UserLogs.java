/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;


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
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static AuthenticationService.userAuth userAuth = new AuthenticationService.userAuth();

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
    
    public UserLogs(String action, String details) {
        this.action = action;
        this.details = details;
    }
    
    //updateUserLog
    public UserLogs(String action, String details, Users userId) {
        this.action = action;
        this.details = details;
        this.userId = userId;
    }
    
    //getUserLogById
    public UserLogs(Integer id, String action, String details, Date createdAt, Users userId) {
        this.id = id;
        this.action = action;
        this.details = details;
        this.createdAt = createdAt;
        this.userId = userId;
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
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

    /*public static Boolean updateUserLogs(UserLogs updatedUserLog, Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateUserLogs");

            spq.registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_action", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_details", String.class, ParameterMode.IN);

            spq.setParameter("p_id", id);
            spq.setParameter("p_action", updatedUserLog.getAction());
            spq.setParameter("p_details", updatedUserLog.getDetails());

            spq.execute();

            em.getTransaction().commit();

            return true;

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Ha hiba van, rollback
            }
            ex.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }
    
    public static UserLogs getUserLogById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getUserLogById");
            spq.registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_id", id);

            spq.execute(); // vége zárásként

            List<Object[]> resultList = spq.getResultList();

            if (AuthenticationService.isDataMissing(resultList)) {
                return null;
            }

            UserLogs toReturn = new UserLogs();

            for (Object[] record : resultList) {
                System.out.println(record[0].toString() + "¤");
                System.out.println(record[1].toString()+ "¤");
                System.out.println(record[2].toString()+ "¤");
                System.out.println(record[3].toString()+ "¤");
                System.out.println(record[4].toString()+ "¤");
                
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));
                
                UserLogs u = new UserLogs(
                        Integer.valueOf(record[0].toString()),// id
                        record[2].toString(),// Action
                        record[3].toString(),// Details
                        record[4] == null ? null : formatter.parse(record[4].toString()),// created_at
                        user
                );
                toReturn = u;
            }
            return toReturn;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Ha hiba van, rollback
            }
            ex.printStackTrace();
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }*/
}
