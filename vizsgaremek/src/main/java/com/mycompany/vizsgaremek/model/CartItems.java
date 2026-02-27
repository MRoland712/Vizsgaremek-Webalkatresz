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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author neblgergo
 */
@Entity
@Table(name = "cart_items")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CartItems.findAll", query = "SELECT c FROM CartItems c"),
    @NamedQuery(name = "CartItems.findById", query = "SELECT c FROM CartItems c WHERE c.id = :id"),
    @NamedQuery(name = "CartItems.findByQuantity", query = "SELECT c FROM CartItems c WHERE c.quantity = :quantity"),
    @NamedQuery(name = "CartItems.findByAddedAt", query = "SELECT c FROM CartItems c WHERE c.addedAt = :addedAt"),
    @NamedQuery(name = "CartItems.findByIsDeleted", query = "SELECT c FROM CartItems c WHERE c.isDeleted = :isDeleted"),
    @NamedQuery(name = "CartItems.findByDeletedAt", query = "SELECT c FROM CartItems c WHERE c.deletedAt = :deletedAt")})
public class CartItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "added_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedAt;
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

    public CartItems() {
    }

    public CartItems(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
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

    public CartItems(Integer id, Integer quantity, Date addedAt, Date deletedAt, Boolean isDeleted, Parts partId, Users userId) {
        this.id = id;
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.deletedAt = deletedAt;
        this.isDeleted = isDeleted;
        this.partId = partId;
        this.userId = userId;
    }

    public CartItems(Integer quantity, Parts partId, Users userId) {
        this.quantity = quantity;
        this.partId = partId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CartItems)) {
            return false;
        }
        CartItems other = (CartItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.CartItems[ id=" + id + " ]";
    }

    public static Boolean createCartItems(CartItems createdCartItems) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createCartItems");

            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("quantityIN", Integer.class, ParameterMode.IN);

            spq.setParameter("userIdIN", createdCartItems.getUserId().getId());
            spq.setParameter("partIdIN", createdCartItems.getPartId().getId());
            spq.setParameter("quantityIN", createdCartItems.getQuantity());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<CartItems> getAllCartItems() {
        EntityManager em = emf.createEntityManager();

        try {
            //eljárást meghívjuk
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllCartItems");
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            ArrayList<CartItems> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Parts part = new Parts();
                part.setId(Integer.valueOf(record[2].toString()));

                CartItems ci = new CartItems(
                        Integer.valueOf(record[0].toString()), // 1. id
                        Integer.valueOf(record[3] != null ? record[3].toString() : null), // 2. quantity
                        record[4] == null ? null : formatter.parse(record[4].toString()), // 4. added_At
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 5. deletedAt
                        Boolean.FALSE, // isDeleted
                        part,
                        user
                );

                toReturn.add(ci);
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

    public static CartItems getCartItemById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getCartItemById");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            //  Üres lista ellenőrzés!
            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            Object[] record = resultList.get(0);

            Users user = new Users();
            user.setId(Integer.valueOf(record[1].toString()));

            Parts part = new Parts();
            part.setId(Integer.valueOf(record[2].toString()));

            CartItems ci = new CartItems(
                    Integer.valueOf(record[0].toString()), // id
                    record[3] == null ? null : Integer.valueOf(record[3].toString()), // quantity 
                    record[4] == null ? null : formatter.parse(record[4].toString()), // added_at 
                    record[6] == null ? null : formatter.parse(record[6].toString()), // deleted_at 
                    record[5] != null && Boolean.parseBoolean(record[5].toString()), // is_deleted 
                    part,
                    user
            );
            return ci;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Boolean softDeleteCartItem(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteCartItem");
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

    public static Boolean updateCartItem(CartItems updatedCartItems) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateCartItem");

            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("userId", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("partId", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("quantityIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isDeleted", Integer.class, ParameterMode.IN);

            spq.setParameter("idIN", updatedCartItems.getId());
            spq.setParameter("userId", updatedCartItems.getUserId().getId());
            spq.setParameter("partId", updatedCartItems.getPartId().getId());
            spq.setParameter("quantityIN", updatedCartItems.getQuantity());
            spq.setParameter("isDeleted", Boolean.TRUE.equals(updatedCartItems.getIsDeleted()) ? 1 : 0);

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

    public static ArrayList<CartItems> getCartItemsByUserId(Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getCartItemsByUserId");
            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("userIdIN", userId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            ArrayList<CartItems> cartItemsList = new ArrayList<>();

            for (Object[] record : resultList) {
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Parts part = new Parts();
                part.setId(Integer.valueOf(record[2].toString()));
                part.setName(record[7] != null ? record[7].toString() : "Ismeretlen termék");
                part.setPrice(record[8] != null ? new java.math.BigDecimal(record[8].toString()) : null);

                CartItems ci = new CartItems(
                        Integer.valueOf(record[0].toString()),
                        record[3] == null ? null : Integer.valueOf(record[3].toString()),
                        record[4] == null ? null : formatter.parse(record[4].toString()),
                        record[6] == null ? null : formatter.parse(record[6].toString()),
                        record[5] != null && Boolean.parseBoolean(record[5].toString()),
                        part,
                        user
                );
                cartItemsList.add(ci);
            }

            return cartItemsList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static ArrayList<CartItems> getCartItemsByPartId(Integer partId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getCartItemsByPartId");
            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("partIdIN", partId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            ArrayList<CartItems> cartItemsList = new ArrayList<>();

            for (Object[] record : resultList) {
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Parts part = new Parts();
                part.setId(Integer.valueOf(record[2].toString()));

                CartItems ci = new CartItems(
                        Integer.valueOf(record[0].toString()),
                        record[3] == null ? null : Integer.valueOf(record[3].toString()),
                        record[4] == null ? null : formatter.parse(record[4].toString()),
                        record[6] == null ? null : formatter.parse(record[6].toString()),
                        record[5] != null && Boolean.parseBoolean(record[5].toString()),
                        part,
                        user
                );
                cartItemsList.add(ci);
            }

            return cartItemsList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

}
