/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
@Table(name = "user_vehicles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserVehicles.findAll", query = "SELECT u FROM UserVehicles u"),
    @NamedQuery(name = "UserVehicles.findById", query = "SELECT u FROM UserVehicles u WHERE u.id = :id"),
    @NamedQuery(name = "UserVehicles.findByUserId", query = "SELECT u FROM UserVehicles u WHERE u.userId = :userId"),
    @NamedQuery(name = "UserVehicles.findByVehicleType", query = "SELECT u FROM UserVehicles u WHERE u.vehicleType = :vehicleType"),
    @NamedQuery(name = "UserVehicles.findByVehicleId", query = "SELECT u FROM UserVehicles u WHERE u.vehicleId = :vehicleId"),
    @NamedQuery(name = "UserVehicles.findByYear", query = "SELECT u FROM UserVehicles u WHERE u.year = :year"),
    @NamedQuery(name = "UserVehicles.findByCreatedAt", query = "SELECT u FROM UserVehicles u WHERE u.createdAt = :createdAt"),
    @NamedQuery(name = "UserVehicles.findByIsDeleted", query = "SELECT u FROM UserVehicles u WHERE u.isDeleted = :isDeleted"),
    @NamedQuery(name = "UserVehicles.findByDeletedAt", query = "SELECT u FROM UserVehicles u WHERE u.deletedAt = :deletedAt")})
public class UserVehicles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "vehicle_type")
    private String vehicleType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vehicle_id")
    private int vehicleId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "year")
    private int year;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_deleted")
    private boolean isDeleted;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public UserVehicles() {
    }

    public UserVehicles(Integer id) {
        this.id = id;
    }

    public UserVehicles(Integer id, int userId, String vehicleType, int vehicleId, int year, Date createdAt, boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.vehicleType = vehicleType;
        this.vehicleId = vehicleId;
        this.year = year;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getIsDeleted() {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserVehicles)) {
            return false;
        }
        UserVehicles other = (UserVehicles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.UserVehicles[ id=" + id + " ]";
    }

    public UserVehicles(int userId, String vehicleType, int vehicleId, int year) {
        this.userId = userId;
        this.vehicleType = vehicleType;
        this.vehicleId = vehicleId;
        this.year = year;
    }

    public UserVehicles(Integer id, int userId, String vehicleType, int vehicleId, int year, Date createdAt, boolean isDeleted, Date deletedAt) {
        this.id = id;
        this.userId = userId;
        this.vehicleType = vehicleType;
        this.vehicleId = vehicleId;
        this.year = year;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }
    
    
    
    // createUserVehicle
    public static Boolean createUserVehicle(UserVehicles uv) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createUserVehicle");

            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("vehicleTypeIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("vehicleIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("yearIN", Integer.class, ParameterMode.IN);

            spq.setParameter("userIdIN", uv.getUserId());
            spq.setParameter("vehicleTypeIN", uv.getVehicleType());
            spq.setParameter("vehicleIdIN", uv.getVehicleId());
            spq.setParameter("yearIN", uv.getYear());

            spq.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // getUserVehiclesByUserId
    public static ArrayList<UserVehicles> getUserVehiclesByUserId(Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getUserVehiclesByUserId");
            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("userIdIN", userId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            ArrayList<UserVehicles> toReturn = new ArrayList<>();

            for (Object[] record : resultList) {
                UserVehicles uv = new UserVehicles(
                    Integer.valueOf(record[0].toString()),// id
                    Integer.valueOf(record[1].toString()),  // user_id
                    record[2] != null ? record[2].toString() : null, // vehicle_type
                    Integer.valueOf(record[3].toString()), // vehicle_id
                    Integer.valueOf(record[4].toString()), // year
                    record[5] == null ? null : formatter.parse(record[5].toString()),  // created_at
                    Boolean.FALSE, // is_deleted
                    record[7] == null ? null : formatter.parse(record[7].toString())  // deleted_at
                );
                toReturn.add(uv);
            }
            return toReturn;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    // softDeleteUserVehicle
    public static Boolean softDeleteUserVehicle(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteUserVehicle");
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
    
}
