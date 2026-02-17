/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "payments")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Payments.findAll", query = "SELECT p FROM Payments p"),
    @NamedQuery(name = "Payments.findById", query = "SELECT p FROM Payments p WHERE p.id = :id"),
    @NamedQuery(name = "Payments.findByAmount", query = "SELECT p FROM Payments p WHERE p.amount = :amount"),
    @NamedQuery(name = "Payments.findByMethod", query = "SELECT p FROM Payments p WHERE p.method = :method"),
    @NamedQuery(name = "Payments.findByStatus", query = "SELECT p FROM Payments p WHERE p.status = :status"),
    @NamedQuery(name = "Payments.findByPaidAt", query = "SELECT p FROM Payments p WHERE p.paidAt = :paidAt"),
    @NamedQuery(name = "Payments.findByCreatedAt", query = "SELECT p FROM Payments p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "Payments.findByIsDeleted", query = "SELECT p FROM Payments p WHERE p.isDeleted = :isDeleted"),
    @NamedQuery(name = "Payments.findByDeletedAt", query = "SELECT p FROM Payments p WHERE p.deletedAt = :deletedAt")})
public class Payments implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "amount")
    private BigDecimal amount;
    @Size(max = 50)
    @Column(name = "method")
    private String method;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    @Column(name = "paid_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Orders orderId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paymentId")
    private Collection<Refunds> refundsCollection;

    public Payments() {
    }
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public Payments(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Date paidAt) {
        this.paidAt = paidAt;
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

    public Orders getOrderId() {
        return orderId;
    }

    public void setOrderId(Orders orderId) {
        this.orderId = orderId;
    }

    @XmlTransient
    public Collection<Refunds> getRefundsCollection() {
        return refundsCollection;
    }

    public void setRefundsCollection(Collection<Refunds> refundsCollection) {
        this.refundsCollection = refundsCollection;
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
        if (!(object instanceof Payments)) {
            return false;
        }
        Payments other = (Payments) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Payments(Integer id, BigDecimal amount, String method, String status, Date paidAt, Date createdAt, Boolean isDeleted, Date deletedAt, Orders orderId) {
        this.id = id;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.orderId = orderId;
    }

    public Payments(BigDecimal amount, String method, String status, Date paidAt, Orders orderId) {
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.paidAt = paidAt;
        this.orderId = orderId;
    }
    
    
    

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Payments[ id=" + id + " ]";
    }
    
    public static Boolean createPayments(Payments createdPayments) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createPayments");

            spq.registerStoredProcedureParameter("orderIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("amountIN", BigDecimal.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("methodIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("statusIN", String.class, ParameterMode.IN);

            spq.setParameter("orderIdIN", createdPayments.getOrderId().getId());
            spq.setParameter("amountIN", createdPayments.getAmount());
            spq.setParameter("methodIN", createdPayments.getMethod());
            spq.setParameter("statusIN", createdPayments.getStatus());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Payments> getAllPayments() {
        EntityManager em = emf.createEntityManager();

        try {
            //eljárást meghívjuk
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllPayments");
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            ArrayList<Payments> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Orders order = new Orders();
                order.setId(Integer.valueOf(record[1].toString()));

                Payments p = new Payments(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] == null ? null : new BigDecimal(record[2].toString()), // 3. amount
                        record[3] != null ? record[3].toString() : null, // 4. method
                        record[4] != null ? record[4].toString() : null, // 5. status
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 6. paid_at
                        record[6] == null ? null : formatter.parse(record[6].toString()), // 7. created_at
                        Boolean.FALSE, // isDeleted
                        record[7] == null ? null : formatter.parse(record[7].toString()), // 8. deleted_at
                        order
                );

                toReturn.add(p);
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
    
    public static Payments getPaymentById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPaymentById");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            //  Üres lista ellenőrzés!
            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            Object[] record = resultList.get(0);

            Orders order = new Orders();
            order.setId(Integer.valueOf(record[1].toString()));

            Payments p = new Payments(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] == null ? null : new BigDecimal(record[2].toString()), // 3. amount
                        record[3] != null ? record[3].toString() : null, // 4. method
                        record[4] != null ? record[4].toString() : null, // 5. status
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 6. paid_at
                        record[6] == null ? null : formatter.parse(record[6].toString()), // 7. created_at
                        Boolean.FALSE, // isDeleted
                        record[7] == null ? null : formatter.parse(record[7].toString()), // 8. deleted_at
                        order
            );
            return p;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Boolean softDeletePayment(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeletePayment");
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
    
    public static Payments getPaymentsByOrderId(Integer orderId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPaymentsByOrderId");
            spq.registerStoredProcedureParameter("orderId", Integer.class, ParameterMode.IN);
            spq.setParameter("orderId", orderId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            // Csak EGY rekord van (getById)
            Object[] record = resultList.get(0);

            Orders order = new Orders();
            order.setId(Integer.valueOf(record[1].toString()));

            // Parts objektum létrehozása
            Payments p = new Payments(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] == null ? null : new BigDecimal(record[2].toString()), // 3. amount
                        record[3] != null ? record[3].toString() : null, // 4. method
                        record[4] != null ? record[4].toString() : null, // 5. status
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 6. paid_at
                        record[6] == null ? null : formatter.parse(record[6].toString()), // 7. created_at
                        Boolean.FALSE, // isDeleted
                        record[7] == null ? null : formatter.parse(record[7].toString()), // 8. deleted_at
                        order
            );

            return p;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public static Boolean updatePayment(Payments updatedPayment) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updatePayment");
            
             spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("orderIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("amountIN", BigDecimal.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("methodIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("statusIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isDeleted", Integer.class, ParameterMode.IN);

            spq.setParameter("idIN", updatedPayment.getId());
            spq.setParameter("orderIdIN", updatedPayment.getOrderId().getId());
            spq.setParameter("amountIN", updatedPayment.getAmount());
            spq.setParameter("methodIN", updatedPayment.getMethod());
            spq.setParameter("statusIN", updatedPayment.getStatus());
            spq.setParameter("isDeleted", Boolean.TRUE.equals(updatedPayment.getIsDeleted()) ? 1 : 0);

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
