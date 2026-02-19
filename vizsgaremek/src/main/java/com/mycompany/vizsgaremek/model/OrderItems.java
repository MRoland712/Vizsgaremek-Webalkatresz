/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;


import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "order_items")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OrderItems.findAll", query = "SELECT o FROM OrderItems o"),
    @NamedQuery(name = "OrderItems.findById", query = "SELECT o FROM OrderItems o WHERE o.id = :id"),
    @NamedQuery(name = "OrderItems.findByQuantity", query = "SELECT o FROM OrderItems o WHERE o.quantity = :quantity"),
    @NamedQuery(name = "OrderItems.findByPrice", query = "SELECT o FROM OrderItems o WHERE o.price = :price"),
    @NamedQuery(name = "OrderItems.findByCreatedAt", query = "SELECT o FROM OrderItems o WHERE o.createdAt = :createdAt"),
    @NamedQuery(name = "OrderItems.findByIsDeleted", query = "SELECT o FROM OrderItems o WHERE o.isDeleted = :isDeleted"),
    @NamedQuery(name = "OrderItems.findByDeletedAt", query = "SELECT o FROM OrderItems o WHERE o.deletedAt = :deletedAt")})
public class OrderItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "quantity")
    private Integer quantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private BigDecimal price;
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
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Parts partId;

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public OrderItems(Integer quantity, Parts partId) {
        this.quantity = quantity;
        this.partId = partId;
    }

    public OrderItems(Integer id, Integer quantity, BigDecimal price, Date createdAt, Boolean isDeleted, Date deletedAt, Orders orderId, Parts partId) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.orderId = orderId;
        this.partId = partId;
    }
    
    public OrderItems() {
    }

    public OrderItems(Integer id) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderItems)) {
            return false;
        }
        OrderItems other = (OrderItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.OrderItems[ id=" + id + " ]";
    }
    
    public static ArrayList<OrderItems> getAllOrderItems() {
        EntityManager em = emf.createEntityManager();

        try {
            //eljárást meghívjuk
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllOrderItems");
            spq.execute();

            //Amit visszakap információt berakni egy Object[] list-be
            List<Object[]> resultList = spq.getResultList();

            ArrayList<OrderItems> toReturn = new ArrayList();
            
            for (Object[] record : resultList) {
                
                System.out.println("records: "
                        +"record0 " + (record[0] != null ? record[0].toString() : null)
                        +"record1 " + (record[1] != null ? record[1].toString() : null)
                        +"record2 " + (record[2] != null ? record[2].toString() : null)
                        +"record3 " + (record[3] != null ? record[3].toString() : null)
                        +"record4 " + (record[4] != null ? record[4].toString() : null)
                        +"record5 " + (record[5] != null ? record[5].toString() : null)
                        +"record6 " + (record[6] != null ? record[6].toString() : null)
                        +"record7 " + (record[7] != null ? record[7].toString() : null)
                        );
                
                Orders order = new Orders();
                order.setId(Integer.valueOf(record[6].toString()));
                
                Parts part = new Parts();
                part.setId(Integer.valueOf(record[7].toString()));
                
                OrderItems o = new OrderItems(
                        Integer.valueOf(record[0].toString()), // id
                        Integer.valueOf(record[1].toString()), // quantity
                        new BigDecimal(record[2].toString()), // Price
                        formatter.parse(record[3].toString()), // createdAt
                        Boolean.valueOf(record[4].toString()), // isDeleted
                        record[5] == null ? null : formatter.parse(record[5].toString()), // deletedAt
                        order,
                        part
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
}
