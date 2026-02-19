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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author neblgergo
 */
@Entity
@Table(name = "invoices")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Invoices.findAll", query = "SELECT i FROM Invoices i"),
    @NamedQuery(name = "Invoices.findById", query = "SELECT i FROM Invoices i WHERE i.id = :id"),
    @NamedQuery(name = "Invoices.findByPdfUrl", query = "SELECT i FROM Invoices i WHERE i.pdfUrl = :pdfUrl"),
    @NamedQuery(name = "Invoices.findByCreatedAt", query = "SELECT i FROM Invoices i WHERE i.createdAt = :createdAt"),
    @NamedQuery(name = "Invoices.findByIsDeleted", query = "SELECT i FROM Invoices i WHERE i.isDeleted = :isDeleted"),
    @NamedQuery(name = "Invoices.findByDeletedAt", query = "SELECT i FROM Invoices i WHERE i.deletedAt = :deletedAt")})
public class Invoices implements Serializable {
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "pdf_url")
    private String pdfUrl;
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

    public Invoices() {
    }

    public Invoices(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
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

    public Invoices(Integer id, String pdfUrl, Date createdAt, Boolean isDeleted, Date deletedAt, Orders orderId) {
        this.id = id;
        this.pdfUrl = pdfUrl;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.orderId = orderId;
    }

    public Invoices(String pdfUrl, Orders orderId) {
        this.pdfUrl = pdfUrl;
        this.orderId = orderId;
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
        if (!(object instanceof Invoices)) {
            return false;
        }
        Invoices other = (Invoices) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Invoices[ id=" + id + " ]";
    }
    
    public static Boolean createInvoice(Invoices createdInvoices) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createInvoice");

            spq.registerStoredProcedureParameter("orderIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("pdfUrlIN", String.class, ParameterMode.IN);

            spq.setParameter("orderIdIN", createdInvoices.getOrderId().getId());
            spq.setParameter("pdfUrlIN", createdInvoices.getPdfUrl());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    
    public static ArrayList<Invoices> getAllInvoices() {
        EntityManager em = emf.createEntityManager();

        try {
            //eljárást meghívjuk
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllInvoices");
            spq.execute();

            //Amit visszakap információt berakni egy Object[] list-be
            List<Object[]> resultList = spq.getResultList();

            ArrayList<Invoices> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Orders order = new Orders();
                order.setId(Integer.valueOf(record[1].toString()));

                Invoices i = new Invoices(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] != null ? record[2].toString() : null, // 2. pdfUrl
                        record[3] == null ? null : formatter.parse(record[3].toString()), // 11. createdAt
                        Boolean.FALSE, // 13. isDeleted
                        record[5] == null ? null : formatter.parse(record[5].toString()), // deleted_at
                        order 
                );

                toReturn.add(i);
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
    
    public static Invoices getInvoiceById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getInvoiceById");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            // Csak EGY rekord van (getById)
            Object[] record = resultList.get(0);

            // Users objektum létrehozása
            Orders order = new Orders();
            order.setId(Integer.valueOf(record[1].toString()));

            Invoices i = new Invoices(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] != null ? record[2].toString() : null, // 2. pdfUrl
                        record[3] == null ? null : formatter.parse(record[3].toString()), // 11. createdAt
                        Boolean.FALSE, // 13. isDeleted
                        record[5] == null ? null : formatter.parse(record[5].toString()), // deleted_at
                        order 
            );

            return i;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public static Boolean softDeleteInvoice(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteInvoice");
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

    public static Boolean updateInvoice (Invoices updatedInvoice) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateInvoice");

            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("orderIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("pdfUrlIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isDeletedIN", Integer.class, ParameterMode.IN);

            spq.setParameter("idIN", updatedInvoice.getId());
            spq.setParameter("orderIdIN", updatedInvoice.getOrderId().getId());
            spq.setParameter("pdfUrlIN", updatedInvoice.getPdfUrl());
            spq.setParameter("isDeletedIN", Boolean.TRUE.equals(updatedInvoice.getIsDeleted()) ? 1 : 0);



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
    
    public static ArrayList<Invoices> getInvoicesByOrderId(Integer orderId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getInvoicesByOrderId");
            spq.registerStoredProcedureParameter("orderId", Integer.class, ParameterMode.IN);
            spq.setParameter("orderId", orderId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();
            ArrayList<Invoices> toReturn = new ArrayList();

             for (Object[] record : resultList) {
                Orders order = new Orders();
                order.setId(Integer.valueOf(record[1].toString()));

                Invoices i = new Invoices(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] != null ? record[2].toString() : null, // 2. pdfUrl
                        record[3] == null ? null : formatter.parse(record[3].toString()), // 11. createdAt
                        Boolean.FALSE, // 13. isDeleted
                        record[5] == null ? null : formatter.parse(record[5].toString()), // deleted_at
                        order 
                );

                toReturn.add(i);
            }
            return toReturn;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
}
