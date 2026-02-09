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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
@Table(name = "reviews")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reviews.findAll", query = "SELECT r FROM Reviews r"),
    @NamedQuery(name = "Reviews.findById", query = "SELECT r FROM Reviews r WHERE r.id = :id"),
    @NamedQuery(name = "Reviews.findByRating", query = "SELECT r FROM Reviews r WHERE r.rating = :rating"),
    @NamedQuery(name = "Reviews.findByCreatedAt", query = "SELECT r FROM Reviews r WHERE r.createdAt = :createdAt"),
    @NamedQuery(name = "Reviews.findByIsDeleted", query = "SELECT r FROM Reviews r WHERE r.isDeleted = :isDeleted"),
    @NamedQuery(name = "Reviews.findByDeletedAt", query = "SELECT r FROM Reviews r WHERE r.deletedAt = :deletedAt")})
public class Reviews implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "rating")
    private Integer rating;
    @Lob
    @Size(max = 65535)
    @Column(name = "comment")
    private String comment;
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
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Reviews() {
    }

    public Reviews(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        if (!(object instanceof Reviews)) {
            return false;
        }
        Reviews other = (Reviews) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Reviews(Integer rating, String comment, Parts partId, Users userId) {
        this.rating = rating;
        this.comment = comment;
        this.partId = partId;
        this.userId = userId;
    }

    public Reviews(Integer id, Users userId, Parts partId, Integer rating, String comment, Date createdAt, Date deletedAt, Boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.partId = partId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.isDeleted = isDeleted;

    }

    public Reviews(Integer rating, String comment, Boolean isDeleted) {
        this.rating = rating;
        this.comment = comment;
        this.isDeleted = isDeleted;
    }
    
    

    public Reviews(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Reviews[ id=" + id + " ]";
    }

    public static Boolean createReviews(Reviews createdReviews) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createReviews");

            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("ratingIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("commentIN", String.class, ParameterMode.IN);

            spq.setParameter("userIdIN", createdReviews.getUserId().getId());
            spq.setParameter("partIdIN", createdReviews.getPartId().getId());
            spq.setParameter("ratingIN", createdReviews.getRating());
            spq.setParameter("commentIN", createdReviews.getComment());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Reviews> getAllReviews() {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllReviews");
            spq.execute();
            List<Object[]> resultList = spq.getResultList();
            ArrayList<Reviews> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Parts part = new Parts();
                part.setId(Integer.valueOf(record[2].toString()));

                Reviews r = new Reviews(
                        Integer.valueOf(record[0].toString()), // 0. id
                        user, // 1. user_id 
                        part, // 2. part_id 
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 3. rating
                        record[4] != null ? record[4].toString() : null, // 4. comment
                        record[5] != null ? formatter.parse(record[5].toString()) : null, // 5. created_at
                        record[7] != null ? formatter.parse(record[7].toString()) : null, // 7. deleted_at 
                        Boolean.valueOf(record[6].toString()) // 6. is_deleted
                );

                toReturn.add(r);
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

    public static Reviews getReviewsById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getReviewsById");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);

            Users user = new Users();
            user.setId(Integer.valueOf(record[1].toString()));

            Parts part = new Parts();
            part.setId(Integer.valueOf(record[2].toString()));

            // Parts objektum létrehozása
            Reviews r = new Reviews(
                    Integer.valueOf(record[0].toString()), // 0. id
                    user, // 1. user_id 
                    part, // 2. part_id 
                    record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 3. rating
                    record[4] != null ? record[4].toString() : null, // 4. comment
                    record[5] != null ? formatter.parse(record[5].toString()) : null, // 5. created_at
                    record[7] != null ? formatter.parse(record[7].toString()) : null, // 7. deleted_at 
                    Boolean.valueOf(record[6].toString()) // 6. is_deleted
            );

            return r;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Reviews> getReviewsByPartId(Integer partId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getReviewsByPartId");
            spq.registerStoredProcedureParameter("part_IdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("part_IdIN", partId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();
            ArrayList<Reviews> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Parts part = new Parts();
                part.setId(Integer.valueOf(record[2].toString()));

                Reviews r = new Reviews(
                        Integer.valueOf(record[0].toString()), // 0. id
                        user, // 1. user_id 
                        part, // 2. part_id 
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 3. rating
                        record[4] != null ? record[4].toString() : null, // 4. comment
                        record[5] != null ? formatter.parse(record[5].toString()) : null, // 5. created_at
                        record[7] != null ? formatter.parse(record[7].toString()) : null, // 7. deleted_at 
                        Boolean.valueOf(record[6].toString()) // 6. is_deleted
                );

                toReturn.add(r);
            }
            return toReturn;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Reviews> getReviewsByUserId(Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getReviewsByUserId");
            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("userIdIN", userId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();
            ArrayList<Reviews> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Parts part = new Parts();
                part.setId(Integer.valueOf(record[2].toString()));

                Reviews r = new Reviews(
                        Integer.valueOf(record[0].toString()), // 0. id
                        user, // 1. user_id 
                        part, // 2. part_id 
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 3. rating
                        record[4] != null ? record[4].toString() : null, // 4. comment
                        record[5] != null ? formatter.parse(record[5].toString()) : null, // 5. created_at
                        record[7] != null ? formatter.parse(record[7].toString()) : null, // 7. deleted_at 
                        Boolean.valueOf(record[6].toString()) // 6. is_deleted
                );

                toReturn.add(r);
            }
            return toReturn;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Reviews> getReviewsByRating(Integer rating) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getReviewsByRating");
            spq.registerStoredProcedureParameter("ratingIN", Integer.class, ParameterMode.IN);
            spq.setParameter("ratingIN", rating);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();
            ArrayList<Reviews> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Parts part = new Parts();
                part.setId(Integer.valueOf(record[2].toString()));

                Reviews r = new Reviews(
                        Integer.valueOf(record[0].toString()), // 0. id
                        user, // 1. user_id 
                        part, // 2. part_id 
                        record[3] != null ? Integer.valueOf(record[3].toString()) : null, // 3. rating
                        record[4] != null ? record[4].toString() : null, // 4. comment
                        record[5] != null ? formatter.parse(record[5].toString()) : null, // 5. created_at
                        record[7] != null ? formatter.parse(record[7].toString()) : null, // 7. deleted_at 
                        Boolean.valueOf(record[6].toString()) // 6. is_deleted
                );

                toReturn.add(r);
            }
            return toReturn;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }


    public static Boolean updateReviews(Reviews updatedReviews) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateReviews");

            spq.registerStoredProcedureParameter("review_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("ratingIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("commentIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isDeletedIN", Integer.class, ParameterMode.IN);

            spq.setParameter("review_id", updatedReviews.getId());
            spq.setParameter("ratingIN", updatedReviews.getRating());
            spq.setParameter("commentIN", updatedReviews.getComment());
            spq.setParameter("isDeletedIN", Boolean.TRUE.equals(updatedReviews.getIsDeleted()) ? 1 : 0);

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


    public static Boolean softDeleteReviews(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteReviews");
            spq.registerStoredProcedureParameter("review_IdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("review_IdIN", id);

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
