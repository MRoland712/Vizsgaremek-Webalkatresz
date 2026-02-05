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
@Table(name = "motors")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Motors.findAll", query = "SELECT m FROM Motors m"),
    @NamedQuery(name = "Motors.findById", query = "SELECT m FROM Motors m WHERE m.id = :id"),
    @NamedQuery(name = "Motors.findByBrand", query = "SELECT m FROM Motors m WHERE m.brand = :brand"),
    @NamedQuery(name = "Motors.findByModel", query = "SELECT m FROM Motors m WHERE m.model = :model"),
    @NamedQuery(name = "Motors.findByYearFrom", query = "SELECT m FROM Motors m WHERE m.yearFrom = :yearFrom"),
    @NamedQuery(name = "Motors.findByYearTo", query = "SELECT m FROM Motors m WHERE m.yearTo = :yearTo"),
    @NamedQuery(name = "Motors.findByCreatedAt", query = "SELECT m FROM Motors m WHERE m.createdAt = :createdAt"),
    @NamedQuery(name = "Motors.findByUpdatedAt", query = "SELECT m FROM Motors m WHERE m.updatedAt = :updatedAt"),
    @NamedQuery(name = "Motors.findByIsDeleted", query = "SELECT m FROM Motors m WHERE m.isDeleted = :isDeleted"),
    @NamedQuery(name = "Motors.findByDeletedAt", query = "SELECT m FROM Motors m WHERE m.deletedAt = :deletedAt")})
public class Motors implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "brand")
    private String brand;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "model")
    private String model;
    @Column(name = "year_from")
    private Integer yearFrom;
    @Column(name = "year_to")
    private Integer yearTo;
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
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Motors() {
    }

    public Motors(Integer id) {
        this.id = id;
    }

    public Motors(Integer id, String brand, String model) {
        this.id = id;
        this.brand = brand;
        this.model = model;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
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

    public Motors(String brand, String model, Integer yearFrom, Integer yearTo) {
        this.brand = brand;
        this.model = model;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
    }

    public Motors(Integer id, String brand, String model, Integer yearFrom, Integer yearTo, Date createdAt, Date updatedAt, Boolean isDeleted) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
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
        if (!(object instanceof Motors)) {
            return false;
        }
        Motors other = (Motors) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Motors[ id=" + id + " ]";
    }

    public static Boolean createMotors(Motors createdMotors) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createMotors");

            spq.registerStoredProcedureParameter("brandIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("modelIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("yearFromIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("yearToIN", Integer.class, ParameterMode.IN);

            spq.setParameter("brandIN", createdMotors.getBrand());
            spq.setParameter("modelIN", createdMotors.getModel());
            spq.setParameter("yearFromIN", createdMotors.getYearFrom());
            spq.setParameter("yearToIN", createdMotors.getYearTo());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Motors> getAllMotors() {
        EntityManager em = emf.createEntityManager();

        try {

            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllMotors");
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            ArrayList<Motors> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Motors m = new Motors(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[1] != null ? record[1].toString() : null, // 2. brand
                        record[2] != null ? record[2].toString() : null, // 3. model
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                        record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 9. createdAt
                        record[6] == null ? null : formatter.parse(record[6].toString()), // 10. updatedAt
                        Boolean.FALSE
                );

                toReturn.add(m);
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

    public static Motors getMotorsById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getMotorsById");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);

            Motors m = new Motors(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[1] != null ? record[1].toString() : null, // 2. brand
                    record[2] != null ? record[2].toString() : null, // 3. model
                    record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                    record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                    record[5] == null ? null : formatter.parse(record[5].toString()), // 9. createdAt
                    record[6] == null ? null : formatter.parse(record[6].toString()), // 10. updatedAt
                    Boolean.FALSE
            );

            return m;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Motors getMotorsByBrand(String motors) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getMotorsByBrand");
            spq.registerStoredProcedureParameter("brandIN", String.class, ParameterMode.IN);
            spq.setParameter("brandIN", motors);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);

            Motors m = new Motors(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[1] != null ? record[1].toString() : null, // 2. brand
                    record[2] != null ? record[2].toString() : null, // 3. model
                    record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                    record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                    record[5] == null ? null : formatter.parse(record[5].toString()), // 9. createdAt
                    record[6] == null ? null : formatter.parse(record[6].toString()), // 10. updatedAt
                    Boolean.FALSE
            );

            return m;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Motors getMotorsByModel(String model) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getMotorsByModel");
            spq.registerStoredProcedureParameter("modelIN", String.class, ParameterMode.IN);
            spq.setParameter("modelIN", model);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);

            Motors m = new Motors(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[1] != null ? record[1].toString() : null, // 2. brand
                    record[2] != null ? record[2].toString() : null, // 3. model
                    record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                    record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                    record[5] == null ? null : formatter.parse(record[5].toString()), // 9. createdAt
                    record[6] == null ? null : formatter.parse(record[6].toString()), // 10. updatedAt
                    Boolean.FALSE
            );

            return m;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Boolean softDeleteMotors(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteCars");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);

            spq.execute();
            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

}
