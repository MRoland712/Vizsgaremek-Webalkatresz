/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
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
 * @author neblg
 */
@Entity
@Table(name = "part_images")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PartImages.findAll", query = "SELECT p FROM PartImages p"),
    @NamedQuery(name = "PartImages.findById", query = "SELECT p FROM PartImages p WHERE p.id = :id"),
    @NamedQuery(name = "PartImages.findByUrl", query = "SELECT p FROM PartImages p WHERE p.url = :url"),
    @NamedQuery(name = "PartImages.findByIsPrimary", query = "SELECT p FROM PartImages p WHERE p.isPrimary = :isPrimary"),
    @NamedQuery(name = "PartImages.findByCreatedAt", query = "SELECT p FROM PartImages p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "PartImages.findByIsDeleted", query = "SELECT p FROM PartImages p WHERE p.isDeleted = :isDeleted"),
    @NamedQuery(name = "PartImages.findByDeletedAt", query = "SELECT p FROM PartImages p WHERE p.deletedAt = :deletedAt")})
public class PartImages implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "url")
    private String url;
    @Column(name = "is_primary")
    private Boolean isPrimary;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Parts partId;

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PartImages() {
    }

    public PartImages(Integer id) {
        this.id = id;
    }

    public PartImages(Integer id, String url) {
        this.id = id;
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
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

    public Parts getPartId() {
        return partId;
    }

    public void setPartId(Parts partId) {
        this.partId = partId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    //getAll
    public PartImages(Integer id, String url, Boolean isPrimary, Date createdAt, Boolean isDeleted, Date deletedAt, Parts partId) {
        this.id = id;
        this.url = url;
        this.isPrimary = isPrimary;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.partId = partId;
    }

    //create
    public PartImages(String url, Boolean isPrimary, Parts partId) {
        this.url = url;
        this.isPrimary = isPrimary;
        this.partId = partId;
    }

    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PartImages)) {
            return false;
        }
        PartImages other = (PartImages) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.PartImages[ id=" + id + " ]";
    }

    public static Boolean createPartImages (PartImages createPartImages) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createPartImages");

            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("urlIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isPrimaryIN", Integer.class, ParameterMode.IN);

            spq.setParameter("partIdIN", createPartImages.getPartId().getId());
            spq.setParameter("urlIN", createPartImages.getUrl());
            spq.setParameter("isPrimaryIN", Boolean.TRUE.equals(createPartImages.getIsPrimary()) ? 1 : 0);

            spq.execute();
            
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<PartImages> getAllPartImages() {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllPartImages");
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            ArrayList<PartImages> toReturn = new ArrayList<>();

            for (Object[] record : resultList) {
                Parts parts = new Parts();
                parts.setId(Integer.valueOf(record[1].toString()));
                PartImages img = new PartImages(
                        Integer.valueOf(record[0].toString()), // id
                        record[2].toString(), // url
                        Boolean.valueOf(record[3].toString()), // is_primary
                        record[4] == null ? null : formatter.parse(record[4].toString()), // created_at
                        Boolean.valueOf(record[5].toString()), // is_deleted
                        record[6] == null ? null : formatter.parse(record[6].toString()), // deleted_at
                        parts
                );
                toReturn.add(img);
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

    public static PartImages getPartImagesById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartImagesById");
            spq.registerStoredProcedureParameter("partImages_id", Integer.class, ParameterMode.IN);
            spq.setParameter("partImages_id", id);

            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            PartImages toReturn = null;

            for (Object[] record : resultList) {
                Parts parts = new Parts();
                parts.setId(Integer.valueOf(record[1].toString()));
                PartImages img = new PartImages(
                        Integer.valueOf(record[0].toString()), // id
                        record[2].toString(), // url
                        Boolean.valueOf(record[3].toString()), // is_primary
                        record[4] == null ? null : formatter.parse(record[4].toString()), // created_at
                        Boolean.valueOf(record[5].toString()), // is_deleted
                        record[6] == null ? null : formatter.parse(record[6].toString()),// deleted_at
                        parts
                );
                toReturn = img;
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

    public static ArrayList<PartImages> getPartImagesByPartId(Integer partId) {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartImagesByPartId");
            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("partIdIN", partId);

            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            ArrayList<PartImages> toReturn = new ArrayList<>();

            for (Object[] record : resultList) {
                Parts parts = new Parts();
                parts.setId(Integer.valueOf(record[1].toString()));
                PartImages img = new PartImages(
                        Integer.valueOf(record[0].toString()), // id
                        record[2].toString(), // url
                        Boolean.valueOf(record[3].toString()), // is_primary
                        record[4] == null ? null : formatter.parse(record[4].toString()), // created_at
                        Boolean.valueOf(record[5].toString()), // is_deleted
                        record[6] == null ? null : formatter.parse(record[6].toString()), // deleted_at
                        parts
                );
                toReturn.add(img);
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

    public static Boolean updatePartImages(PartImages updatedImage) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("updatePartImages");

            spq.registerStoredProcedureParameter("partImages_IdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("urlIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isPrimaryIN", Boolean.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isDeletedIN", Boolean.class, ParameterMode.IN);

            spq.setParameter("partImages_IdIN", updatedImage.getId());
            spq.setParameter("urlIN", updatedImage.getUrl());
            spq.setParameter("isPrimaryIN", updatedImage.getIsPrimary());
            spq.setParameter("isDeletedIN", updatedImage.getIsDeleted());

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

    public static Boolean softDeletePartImages(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeletePartImages");
            spq.registerStoredProcedureParameter("partImages_IdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("partImages_IdIN", id);

            spq.execute();

            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println(e);
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

}
