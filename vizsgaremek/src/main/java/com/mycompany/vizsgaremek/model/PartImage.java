package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "part_images")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PartImage.findAll", query = "SELECT p FROM PartImage p"),
    @NamedQuery(name = "PartImage.findById", query = "SELECT p FROM PartImage p WHERE p.id = :id"),
    @NamedQuery(name = "PartImage.findByPartId", query = "SELECT p FROM PartImage p WHERE p.partId = :partId"),
    @NamedQuery(name = "PartImage.findByUrl", query = "SELECT p FROM PartImage p WHERE p.url = :url"),
    @NamedQuery(name = "PartImage.findByIsPrimary", query = "SELECT p FROM PartImage p WHERE p.isPrimary = :isPrimary")
})
public class PartImage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "part_id")
    private Integer partId;
    
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

    // EntityManagerFactory
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PartImage)) {
            return false;
        }
        PartImage other = (PartImage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.PartImage[ id=" + id + " ]";
    }

    public PartImage() {
    }
    
    // createPartImage
    public PartImage(Integer partId, String url, Boolean isPrimary) {
        this.partId = partId;
        this.url = url;
        this.isPrimary = isPrimary;
    }
    
    // getPartImageById / getAllPartImages
    public PartImage(Integer id, Integer partId, String url, Boolean isPrimary, Date createdAt, Boolean isDeleted, Date deletedAt) {
        this.id = id;
        this.partId = partId;
        this.url = url;
        this.isPrimary = isPrimary;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }


    public static Integer createPartImage(PartImage newImage) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createPartImages");

            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("urlIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isPrimaryIN", Boolean.class, ParameterMode.IN);

            spq.setParameter("partIdIN", newImage.getPartId());
            spq.setParameter("urlIN", newImage.getUrl());
            spq.setParameter("isPrimaryIN", newImage.getIsPrimary());

            spq.execute();

            List<Object[]> resultList = spq.getResultList();
            
            if (resultList != null && !resultList.isEmpty()) {
                Object[] record = resultList.get(0);
                return Integer.valueOf(record[0].toString()); // new_partImages_id
            }

            return -1;

        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        } finally {
            em.clear();
            em.close();
        }
    }
    
    public static ArrayList<PartImage> getAllPartImages() {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllPartImages");
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            ArrayList<PartImage> toReturn = new ArrayList<>();

            for (Object[] record : resultList) {
                PartImage img = new PartImage(
                        Integer.valueOf(record[0].toString()), // id
                        Integer.valueOf(record[1].toString()), // part_id
                        record[2].toString(), // url
                        Boolean.valueOf(record[3].toString()), // is_primary
                        record[4] == null ? null : formatter.parse(record[4].toString()), // created_at
                        Boolean.valueOf(record[5].toString()), // is_deleted
                        record[6] == null ? null : formatter.parse(record[6].toString()) // deleted_at
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
    
    public static PartImage getPartImageById(Integer id) {
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

            PartImage toReturn = null;

            for (Object[] record : resultList) {
                PartImage img = new PartImage(
                        Integer.valueOf(record[0].toString()), // id
                        Integer.valueOf(record[1].toString()), // part_id
                        record[2].toString(), // url
                        Boolean.valueOf(record[3].toString()), // is_primary
                        record[4] == null ? null : formatter.parse(record[4].toString()), // created_at
                        Boolean.valueOf(record[5].toString()), // is_deleted
                        record[6] == null ? null : formatter.parse(record[6].toString()) // deleted_at
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
    
    public static ArrayList<PartImage> getPartImagesByPartId(Integer partId) {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartImagesByPartId");
            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("partIdIN", partId);

            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            ArrayList<PartImage> toReturn = new ArrayList<>();

            for (Object[] record : resultList) {
                PartImage img = new PartImage(
                        Integer.valueOf(record[0].toString()), // id
                        Integer.valueOf(record[1].toString()), // part_id
                        record[2].toString(), // url
                        Boolean.valueOf(record[3].toString()), // is_primary
                        record[4] == null ? null : formatter.parse(record[4].toString()), // created_at
                        Boolean.valueOf(record[5].toString()), // is_deleted
                        record[6] == null ? null : formatter.parse(record[6].toString()) // deleted_at
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
    

    public static Boolean updatePartImage(PartImage updatedImage) {
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
    

    public static Boolean softDeletePartImage(Integer id) {
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