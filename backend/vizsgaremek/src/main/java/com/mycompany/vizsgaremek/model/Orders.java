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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author neblgergo
 */
@Entity
@Table(name = "orders")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Orders.findAll", query = "SELECT o FROM Orders o"),
    @NamedQuery(name = "Orders.findById", query = "SELECT o FROM Orders o WHERE o.id = :id"),
    @NamedQuery(name = "Orders.findByStatus", query = "SELECT o FROM Orders o WHERE o.status = :status"),
    @NamedQuery(name = "Orders.findByCreatedAt", query = "SELECT o FROM Orders o WHERE o.createdAt = :createdAt"),
    @NamedQuery(name = "Orders.findByUpdatedAt", query = "SELECT o FROM Orders o WHERE o.updatedAt = :updatedAt"),
    @NamedQuery(name = "Orders.findByIsDeleted", query = "SELECT o FROM Orders o WHERE o.isDeleted = :isDeleted"),
    @NamedQuery(name = "Orders.findByDeletedAt", query = "SELECT o FROM Orders o WHERE o.deletedAt = :deletedAt")})
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderId")
    private Collection<Payments> paymentsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderId")
    private Collection<OrderLogs> orderLogsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderId")
    private Collection<Invoices> invoicesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderId")
    private Collection<OrderItems> orderItemsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderId")
    private Collection<ShippingStatus> shippingStatusCollection;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Orders() {
    }

    public Orders(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public void setUpdatedAt() {
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

    @XmlTransient
    public Collection<Payments> getPaymentsCollection() {
        return paymentsCollection;
    }

    public void setPaymentsCollection(Collection<Payments> paymentsCollection) {
        this.paymentsCollection = paymentsCollection;
    }

    @XmlTransient
    public Collection<OrderLogs> getOrderLogsCollection() {
        return orderLogsCollection;
    }

    public void setOrderLogsCollection(Collection<OrderLogs> orderLogsCollection) {
        this.orderLogsCollection = orderLogsCollection;
    }

    @XmlTransient
    public Collection<Invoices> getInvoicesCollection() {
        return invoicesCollection;
    }

    public void setInvoicesCollection(Collection<Invoices> invoicesCollection) {
        this.invoicesCollection = invoicesCollection;
    }

    @XmlTransient
    public Collection<OrderItems> getOrderItemsCollection() {
        return orderItemsCollection;
    }

    public void setOrderItemsCollection(Collection<OrderItems> orderItemsCollection) {
        this.orderItemsCollection = orderItemsCollection;
    }

    @XmlTransient
    public Collection<ShippingStatus> getShippingStatusCollection() {
        return shippingStatusCollection;
    }

    public void setShippingStatusCollection(Collection<ShippingStatus> shippingStatusCollection) {
        this.shippingStatusCollection = shippingStatusCollection;
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
        if (!(object instanceof Orders)) {
            return false;
        }
        Orders other = (Orders) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Orders(Integer id, String status, Date createdAt, Date updatedAt, Boolean isDeleted, Date deletedAt, Users userId) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.userId = userId;
    }

    public Orders(String status, Boolean isDeleted, Users userId) {
        this.status = status;
        this.isDeleted = isDeleted;
        this.userId = userId;
    }

    public Orders(String status, Users userId) {
        this.status = status;
        this.userId = userId;
    }

    public Orders(Integer id, String status, Boolean isDeleted, Users userId) {
        this.id = id;
        this.status = status;
        this.isDeleted = isDeleted;
        this.userId = userId;
    }

    public Orders(Users userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Orders[ id=" + id + " ]";
    }

    public static Boolean createOrders(Orders createdOrders) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createOrders");

            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("statusIN", String.class, ParameterMode.IN);

            spq.setParameter("userIdIN", createdOrders.getUserId().getId());
            spq.setParameter("statusIN", createdOrders.getStatus());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static Boolean createOrderWithItem(OrderItems createdOrderWithItem, Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createOrderWithItems");

            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("quantityIN", Integer.class, ParameterMode.IN);

            spq.setParameter("userIdIN", userId);
            spq.setParameter("partIdIN", createdOrderWithItem.getPartId().getId());
            spq.setParameter("quantityIN", createdOrderWithItem.getQuantity());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Orders> getAllOrders() {
        EntityManager em = emf.createEntityManager();

        try {
            //eljárást meghívjuk
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllOrders");
            spq.execute();

            //Amit visszakap információt berakni egy Object[] list-be
            List<Object[]> resultList = spq.getResultList();

            ArrayList<Orders> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                // user_id Users objektum létrehozása
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Orders o = new Orders(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] != null ? record[2].toString() : null, // 2. status
                        record[3] == null ? null : formatter.parse(record[3].toString()), // 11. createdAt
                        record[4] == null ? null : formatter.parse(record[4].toString()), // 12. updatedAt
                        Boolean.FALSE, // 13. isDeleted
                        null, // 14. deletedAt
                        user // 15. userId
                );

                toReturn.add(o);
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

    public static Orders getOrdersById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getOrdersById");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            // Csak EGY rekord van (getById)
            Object[] record = resultList.get(0);

            // Users objektum létrehozása
            Users user = new Users();
            user.setId(Integer.valueOf(record[1].toString()));

            Orders o = new Orders(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[2].toString(), // 1. status
                    record[3] == null ? null : formatter.parse(record[3].toString()), // 11. createdAt
                    record[4] == null ? null : formatter.parse(record[4].toString()), // 12. updatedAt
                    Boolean.FALSE, // 13. isDeleted
                    null, // 14. deletedAt
                    user // 15. userId
            );

            return o;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Orders> getOrdersByUserId(Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getOrdersByUserId");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", userId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            ArrayList<Orders> ordersList = new ArrayList<>();

            for (Object[] record : resultList) {
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Orders order = new Orders(
                        Integer.valueOf(record[0].toString()), // id
                        record[2] != null ? record[2].toString() : null, // status
                        record[3] == null ? null : formatter.parse(record[3].toString()), // created_at
                        record[4] == null ? null : formatter.parse(record[4].toString()), // updated_at
                        record[5] != null && Boolean.parseBoolean(record[5].toString()), // is_deleted 
                        record[6] == null ? null : formatter.parse(record[6].toString()), // deleted_at 
                        user
                );
                ordersList.add(order);
            }

            return ordersList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Boolean softDeleteOrders(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteOrders");
            spq.registerStoredProcedureParameter("IdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("IdIN", id);

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

    public static Boolean updateOrders(Orders updatedOrders) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateOrders");

            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("statusIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isDeletedIN", Integer.class, ParameterMode.IN);

            spq.setParameter("idIN", updatedOrders.getId());
            spq.setParameter("statusIN", updatedOrders.getStatus());
            spq.setParameter("isDeletedIN", Boolean.TRUE.equals(updatedOrders.getIsDeleted()) ? 1 : 0);

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

    public static Integer createOrderFromCart(Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createOrderFromCart");
            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.setParameter("userIdIN", userId);
            spq.execute();
            em.getTransaction().commit();

            List resultList = spq.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                // Egy oszlop esetén nem Object[] hanem direkt érték
                Object record = resultList.get(0);
                return Integer.valueOf(record.toString());
            }
            return null;
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

}
