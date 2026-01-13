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

/**
 *
 * @author neblg
 */
@Entity
@Table(name = "manufacturers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Manufacturers.findAll", query = "SELECT m FROM Manufacturers m"),
    @NamedQuery(name = "Manufacturers.findById", query = "SELECT m FROM Manufacturers m WHERE m.id = :id"),
    @NamedQuery(name = "Manufacturers.findByName", query = "SELECT m FROM Manufacturers m WHERE m.name = :name"),
    @NamedQuery(name = "Manufacturers.findByCountry", query = "SELECT m FROM Manufacturers m WHERE m.country = :country"),
    @NamedQuery(name = "Manufacturers.findByCreatedAt", query = "SELECT m FROM Manufacturers m WHERE m.createdAt = :createdAt"),
    @NamedQuery(name = "Manufacturers.findByIsDeleted", query = "SELECT m FROM Manufacturers m WHERE m.isDeleted = :isDeleted"),
    @NamedQuery(name = "Manufacturers.findByDeletedAt", query = "SELECT m FROM Manufacturers m WHERE m.deletedAt = :deletedAt")})
public class Manufacturers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 50)
    @Column(name = "country")
    private String country;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "manufacturerId")
    private Collection<Parts> partsCollection;

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Manufacturers() {
    }

    public Manufacturers(Integer id) {
        this.id = id;
    }

    public Manufacturers(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    //createManufacturers
    public Manufacturers(String name, String country) {
        this.name = name;
        this.country = country;
    }

    //getAllManufacturers & getManufacturersById
    public Manufacturers(Integer id, String name, String country, Date createdAt, Boolean isDeleted, Date deletedAt) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }
    
    //updateManufacturers

    public Manufacturers(Integer id, String name, String country, Boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.isDeleted = isDeleted;
    }
    

    @XmlTransient
    public Collection<Parts> getPartsCollection() {
        return partsCollection;
    }

    public void setPartsCollection(Collection<Parts> partsCollection) {
        this.partsCollection = partsCollection;
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
        if (!(object instanceof Manufacturers)) {
            return false;
        }
        Manufacturers other = (Manufacturers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Manufacturers[ id=" + id + " ]";
    }

    public static Boolean createManufacturers(Manufacturers createdManufacturers) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createManufacturers");

            spq.registerStoredProcedureParameter("p_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_country", String.class, ParameterMode.IN);

            spq.setParameter("p_name", createdManufacturers.getName());
            spq.setParameter("p_country", createdManufacturers.getCountry());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Manufacturers> getAllManufacturers() {
        EntityManager em = emf.createEntityManager();

        try {
            
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllManufacturers");
            spq.execute();

            
            List<Object[]> resultList = spq.getResultList();

            ArrayList<Manufacturers> toReturn = new ArrayList();

            for (Object[] record : resultList) {

                Manufacturers m = new Manufacturers(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[1] != null ? record[1].toString() : null, // 2. name
                        record[2] != null ? record[2].toString() : null, // 3. country
                        record[3] == null ? null : formatter.parse(record[3].toString()), // 11. createdAt
                        Boolean.FALSE, // 13. isDeleted
                        null // 14. deletedAt
                );

                toReturn.add(m);
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
    }
    
    public static Manufacturers getManufacturersById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getManufacturersById");
            spq.registerStoredProcedureParameter("p_manufacturers_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_manufacturers_id", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            // Csak EGY rekord van (getById)
            Object[] record = resultList.get(0);


            Manufacturers m = new Manufacturers(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[1] != null ? record[1].toString() : null, // 2. name
                        record[2] != null ? record[2].toString() : null, // 3. country
                        record[3] == null ? null : formatter.parse(record[3].toString()), // 11. createdAt
                        Boolean.FALSE, // 13. isDeleted
                        null // 14. deletedAt
               
            );

            return m;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public static Boolean softDeleteManufacturers(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteManufacturers");
            spq.registerStoredProcedureParameter("p_manufacturers_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_manufacturers_id", id);

            spq.execute();
            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // ha hiba van, rollback
            }
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }
    
    
    public static Boolean updateManufacturers(Manufacturers updatedManufacturers) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateManufacturers");
            
           
            spq.registerStoredProcedureParameter("p_manufacturers_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_country", String.class, ParameterMode.IN);

            
            
            spq.setParameter("p_manufacturers_id", updatedManufacturers.getId());
            spq.setParameter("p_name", updatedManufacturers.getName());
            spq.setParameter("p_country", updatedManufacturers.getCountry());
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

}
