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
@Table(name = "cars")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cars.findAll", query = "SELECT c FROM Cars c"),
    @NamedQuery(name = "Cars.findById", query = "SELECT c FROM Cars c WHERE c.id = :id"),
    @NamedQuery(name = "Cars.findByBrand", query = "SELECT c FROM Cars c WHERE c.brand = :brand"),
    @NamedQuery(name = "Cars.findByModel", query = "SELECT c FROM Cars c WHERE c.model = :model"),
    @NamedQuery(name = "Cars.findByYearFrom", query = "SELECT c FROM Cars c WHERE c.yearFrom = :yearFrom"),
    @NamedQuery(name = "Cars.findByYearTo", query = "SELECT c FROM Cars c WHERE c.yearTo = :yearTo"),
    @NamedQuery(name = "Cars.findByCreatedAt", query = "SELECT c FROM Cars c WHERE c.createdAt = :createdAt"),
    @NamedQuery(name = "Cars.findByUpdatedAt", query = "SELECT c FROM Cars c WHERE c.updatedAt = :updatedAt"),
    @NamedQuery(name = "Cars.findByIsDeleted", query = "SELECT c FROM Cars c WHERE c.isDeleted = :isDeleted"),
    @NamedQuery(name = "Cars.findByDeletedAt", query = "SELECT c FROM Cars c WHERE c.deletedAt = :deletedAt")})
public class Cars implements Serializable {

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

    public Cars() {
    }

    public Cars(Integer id) {
        this.id = id;
    }

    public Cars(Integer id, String brand, String model) {
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

    //createCars
    public Cars(String brand, String model, Integer yearFrom, Integer yearTo) {
        this.brand = brand;
        this.model = model;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
    }

    public Cars(Integer id, String brand, String model, Integer yearFrom, Integer yearTo, Date createdAt, Date updatedAt, Boolean isDeleted) {
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
        if (!(object instanceof Cars)) {
            return false;
        }
        Cars other = (Cars) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Cars[ id=" + id + " ]";
    }

    public static Boolean createCars(Cars createdCars) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createCars");

            spq.registerStoredProcedureParameter("brandIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("modelIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("yearFromIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("yearToIN", Integer.class, ParameterMode.IN);

            spq.setParameter("brandIN", createdCars.getBrand());
            spq.setParameter("modelIN", createdCars.getModel());
            spq.setParameter("yearFromIN", createdCars.getYearFrom());
            spq.setParameter("yearToIN", createdCars.getYearTo());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Cars> getAllCars() {
        EntityManager em = emf.createEntityManager();

        try {
           
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllCars");
            spq.execute();


            List<Object[]> resultList = spq.getResultList();

            ArrayList<Cars> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Cars c = new Cars(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[1] != null ? record[1].toString() : null, // 2. brand
                        record[2] != null ? record[2].toString() : null, // 3. model
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                        record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 9. createdAt
                        record[6] == null ? null : formatter.parse(record[6].toString()), // 10. updatedAt
                        Boolean.FALSE
                );

                toReturn.add(c);
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
    
    public static Cars getCarsById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getCarsById");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);


            Cars c = new Cars(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[1] != null ? record[1].toString() : null, // 2. brand
                        record[2] != null ? record[2].toString() : null, // 3. model
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                        record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 9. createdAt
                        record[6] == null ? null : formatter.parse(record[6].toString()), // 10. updatedAt
                        Boolean.FALSE
               
            );

            return c;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public static Cars getCarsByBrand(String brand) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getCarsByBrand");
            spq.registerStoredProcedureParameter("brandIN", String.class, ParameterMode.IN);
            spq.setParameter("brandIN", brand);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);


            Cars c = new Cars(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[1] != null ? record[1].toString() : null, // 2. brand
                        record[2] != null ? record[2].toString() : null, // 3. model
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                        record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 9. createdAt
                        record[6] == null ? null : formatter.parse(record[6].toString()), // 10. updatedAt
                        Boolean.FALSE
               
            );

            return c;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public static Cars getCarsByModel(String model) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getCarsByModel");
            spq.registerStoredProcedureParameter("modelIN", String.class, ParameterMode.IN);
            spq.setParameter("modelIN", model);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);


            Cars c = new Cars(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[1] != null ? record[1].toString() : null, // 2. brand
                        record[2] != null ? record[2].toString() : null, // 3. model
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 4. yearFrom 
                        record[4] != null ? Integer.valueOf(record[4].toString()) : null, // 5. yearTo 
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 9. createdAt
                        record[6] == null ? null : formatter.parse(record[6].toString()), // 10. updatedAt
                        Boolean.FALSE
               
            );

            return c;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public static Boolean softDeleteCars(Integer id) {
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
