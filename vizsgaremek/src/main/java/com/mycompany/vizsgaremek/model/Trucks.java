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
@Table(name = "trucks")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Trucks.findAll", query = "SELECT t FROM Trucks t"),
    @NamedQuery(name = "Trucks.findById", query = "SELECT t FROM Trucks t WHERE t.id = :id"),
    @NamedQuery(name = "Trucks.findByBrand", query = "SELECT t FROM Trucks t WHERE t.brand = :brand"),
    @NamedQuery(name = "Trucks.findByModel", query = "SELECT t FROM Trucks t WHERE t.model = :model"),
    @NamedQuery(name = "Trucks.findByYearFrom", query = "SELECT t FROM Trucks t WHERE t.yearFrom = :yearFrom"),
    @NamedQuery(name = "Trucks.findByYearTo", query = "SELECT t FROM Trucks t WHERE t.yearTo = :yearTo"),
    @NamedQuery(name = "Trucks.findByCreatedAt", query = "SELECT t FROM Trucks t WHERE t.createdAt = :createdAt"),
    @NamedQuery(name = "Trucks.findByUpdatedAt", query = "SELECT t FROM Trucks t WHERE t.updatedAt = :updatedAt"),
    @NamedQuery(name = "Trucks.findByIsDeleted", query = "SELECT t FROM Trucks t WHERE t.isDeleted = :isDeleted"),
    @NamedQuery(name = "Trucks.findByDeletedAt", query = "SELECT t FROM Trucks t WHERE t.deletedAt = :deletedAt")})
public class Trucks implements Serializable {

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

    public Trucks() {
    }

    public Trucks(Integer id) {
        this.id = id;
    }

    public Trucks(Integer id, String brand, String model) {
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
    
    public Trucks(String brand, String model, Integer yearFrom, Integer yearTo) {
        this.brand = brand;
        this.model = model;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
    }

    public Trucks(Integer id, String brand, String model, Integer yearFrom, Integer yearTo, Date createdAt, Date updatedAt, Boolean isDeleted) {
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
        if (!(object instanceof Trucks)) {
            return false;
        }
        Trucks other = (Trucks) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Trucks[ id=" + id + " ]";
    }

    public static Boolean createTrucks(Trucks createdTrucks) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createTrucks");

            spq.registerStoredProcedureParameter("brandIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("modelIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("yearFromIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("yearToIN", Integer.class, ParameterMode.IN);

            spq.setParameter("brandIN", createdTrucks.getBrand());
            spq.setParameter("modelIN", createdTrucks.getModel());
            spq.setParameter("yearFromIN", createdTrucks.getYearFrom());
            spq.setParameter("yearToIN", createdTrucks.getYearTo());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Trucks> getAllTrucks() {
        EntityManager em = emf.createEntityManager();

        try {

            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllTrucks");
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            ArrayList<Trucks> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Trucks t = new Trucks(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[1] != null ? record[1].toString() : null, // 2. brand
                        record[2] != null ? record[2].toString() : null, // 3. model
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                        record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 6. createdAt
                        record[6] == null ? null : formatter.parse(record[6].toString()), // 7. updatedAt
                        Boolean.FALSE
                );

                toReturn.add(t);
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

    public static Trucks getTrucksById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getTrucksById");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);

            Trucks t = new Trucks(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[1] != null ? record[1].toString() : null, // 2. brand
                    record[2] != null ? record[2].toString() : null, // 3. model
                    record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                    record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                    record[5] == null ? null : formatter.parse(record[5].toString()), // 6. createdAt
                    record[6] == null ? null : formatter.parse(record[6].toString()), // 7. updatedAt
                    Boolean.FALSE
            );

            return t;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Trucks getTrucksByBrand(String brand) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getTrucksByBrand");
            spq.registerStoredProcedureParameter("brandIN", String.class, ParameterMode.IN);
            spq.setParameter("brandIN", brand);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);

            Trucks t = new Trucks(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[1] != null ? record[1].toString() : null, // 2. brand
                    record[2] != null ? record[2].toString() : null, // 3. model
                    record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                    record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                    record[5] == null ? null : formatter.parse(record[5].toString()), // 6. createdAt
                    record[6] == null ? null : formatter.parse(record[6].toString()), // 7. updatedAt
                    Boolean.FALSE
            );

            return t;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Trucks getTrucksByModel(String model) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getTrucksByModel");
            spq.registerStoredProcedureParameter("modelIN", String.class, ParameterMode.IN);
            spq.setParameter("modelIN", model);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);

            Trucks t = new Trucks(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[1] != null ? record[1].toString() : null, // 2. brand
                    record[2] != null ? record[2].toString() : null, // 3. model
                    record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                    record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                    record[5] == null ? null : formatter.parse(record[5].toString()), // 6. createdAt
                    record[6] == null ? null : formatter.parse(record[6].toString()), // 7. updatedAt
                    Boolean.FALSE
            );

            return t;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Boolean softDeleteTrucks(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteTrucks");
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
