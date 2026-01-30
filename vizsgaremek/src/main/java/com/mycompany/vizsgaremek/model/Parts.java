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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author neblg
 */
@Entity
@Table(name = "parts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Parts.findAll", query = "SELECT p FROM Parts p"),
    @NamedQuery(name = "Parts.findById", query = "SELECT p FROM Parts p WHERE p.id = :id"),
    @NamedQuery(name = "Parts.findBySku", query = "SELECT p FROM Parts p WHERE p.sku = :sku"),
    @NamedQuery(name = "Parts.findByName", query = "SELECT p FROM Parts p WHERE p.name = :name"),
    @NamedQuery(name = "Parts.findByCategory", query = "SELECT p FROM Parts p WHERE p.category = :category"),
    @NamedQuery(name = "Parts.findByPrice", query = "SELECT p FROM Parts p WHERE p.price = :price"),
    @NamedQuery(name = "Parts.findByStock", query = "SELECT p FROM Parts p WHERE p.stock = :stock"),
    @NamedQuery(name = "Parts.findByStatus", query = "SELECT p FROM Parts p WHERE p.status = :status"),
    @NamedQuery(name = "Parts.findByIsActive", query = "SELECT p FROM Parts p WHERE p.isActive = :isActive"),
    @NamedQuery(name = "Parts.findByCreatedAt", query = "SELECT p FROM Parts p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "Parts.findByUpdatedAt", query = "SELECT p FROM Parts p WHERE p.updatedAt = :updatedAt"),
    @NamedQuery(name = "Parts.findByDeletedAt", query = "SELECT p FROM Parts p WHERE p.deletedAt = :deletedAt"),
    @NamedQuery(name = "Parts.findByIsDeleted", query = "SELECT p FROM Parts p WHERE p.isDeleted = :isDeleted")})
public class Parts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "sku")
    private String sku;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 100)
    @Column(name = "category")
    private String category;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "stock")
    private Integer stock;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<CartItems> cartItemsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<PartImages> partImagesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<PartVariants> partVariantsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<Reviews> reviewsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<StockLogs> stockLogsCollection;
    @JoinColumn(name = "manufacturer_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Manufacturers manufacturerId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<WarehouseStock> warehouseStockCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<OrderItems> orderItemsCollection;

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Parts() {
    }

    public Parts(Integer id) {
        this.id = id;
    }

    public Parts(Integer id, String sku, String name, BigDecimal price) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    // createParts
    public Parts(String sku, String name, String category, BigDecimal price, Integer stock, String status, Boolean isActive, Manufacturers manufacturerId) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.isActive = isActive;
        this.manufacturerId = manufacturerId;
    }

    //getAllParts getPartsById
    public Parts(Integer id, String sku, String name, String category, BigDecimal price, Integer stock, String status, Boolean isActive, Date createdAt, Date updatedAt, Date deletedAt, Boolean isDeleted, Manufacturers manufacturerId) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.isDeleted = isDeleted;
        this.manufacturerId = manufacturerId;
    }

    //getPartsByCategory
    public Parts(String category) {
        this.category = category;
    }
    
    //updateParts
    public Parts(Integer id, String sku, String name, String category, BigDecimal price, Integer stock, String status, Boolean isActive, Date updatedAt, Manufacturers manufacturerId) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.isActive = isActive;
        this.updatedAt = updatedAt;
        this.manufacturerId = manufacturerId;
    }
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @XmlTransient
    public Collection<CartItems> getCartItemsCollection() {
        return cartItemsCollection;
    }

    public void setCartItemsCollection(Collection<CartItems> cartItemsCollection) {
        this.cartItemsCollection = cartItemsCollection;
    }

    @XmlTransient
    public Collection<PartImages> getPartImagesCollection() {
        return partImagesCollection;
    }

    public void setPartImagesCollection(Collection<PartImages> partImagesCollection) {
        this.partImagesCollection = partImagesCollection;
    }

    @XmlTransient
    public Collection<PartVariants> getPartVariantsCollection() {
        return partVariantsCollection;
    }

    public void setPartVariantsCollection(Collection<PartVariants> partVariantsCollection) {
        this.partVariantsCollection = partVariantsCollection;
    }

    @XmlTransient
    public Collection<Reviews> getReviewsCollection() {
        return reviewsCollection;
    }

    public void setReviewsCollection(Collection<Reviews> reviewsCollection) {
        this.reviewsCollection = reviewsCollection;
    }

    @XmlTransient
    public Collection<StockLogs> getStockLogsCollection() {
        return stockLogsCollection;
    }

    public void setStockLogsCollection(Collection<StockLogs> stockLogsCollection) {
        this.stockLogsCollection = stockLogsCollection;
    }

    public Manufacturers getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Manufacturers manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    @XmlTransient
    public Collection<WarehouseStock> getWarehouseStockCollection() {
        return warehouseStockCollection;
    }

    public void setWarehouseStockCollection(Collection<WarehouseStock> warehouseStockCollection) {
        this.warehouseStockCollection = warehouseStockCollection;
    }

    @XmlTransient
    public Collection<OrderItems> getOrderItemsCollection() {
        return orderItemsCollection;
    }

    public void setOrderItemsCollection(Collection<OrderItems> orderItemsCollection) {
        this.orderItemsCollection = orderItemsCollection;
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
        if (!(object instanceof Parts)) {
            return false;
        }
        Parts other = (Parts) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Parts[ id=" + id + " ]";
    }

    public static Boolean createParts(Parts createdParts) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createParts");

            spq.registerStoredProcedureParameter("p_manufacturer_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_sku", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_category", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_price", BigDecimal.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_stock", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_is_active", Integer.class, ParameterMode.IN);

            spq.setParameter("p_manufacturer_id", createdParts.getManufacturerId().getId());
            spq.setParameter("p_sku", createdParts.getSku());
            spq.setParameter("p_name", createdParts.getName());
            spq.setParameter("p_category", createdParts.getCategory());
            spq.setParameter("p_price", createdParts.getPrice());
            spq.setParameter("p_stock", createdParts.getStock());
            spq.setParameter("p_status", createdParts.getStatus());
            spq.setParameter("p_is_active", Boolean.TRUE.equals(createdParts.getIsActive()) ? 1 : 0);

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Parts> getAllParts() {
        EntityManager em = emf.createEntityManager();

        try {
            //eljárást meghívjuk
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllParts");
            spq.execute();

            //Amit visszakap információt berakni egy Object[] list-be
            List<Object[]> resultList = spq.getResultList();

            ArrayList<Parts> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                // user_id Users objektum létrehozása
                Manufacturers manufacturer = new Manufacturers();
                manufacturer.setId(Integer.valueOf(record[1].toString()));

                Parts p = new Parts(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] != null ? record[2].toString() : null, // 2. sku
                        record[3] != null ? record[3].toString() : null, // 3. name
                        record[4] != null ? record[4].toString() : null, // 4. category 
                        record[5] != null ? new BigDecimal(record[5].toString()) : null, // 5. price 
                        record[6] != null ? Integer.valueOf(record[6].toString()) : null, // 6. stock 
                        record[7] != null ? record[7].toString() : null, // 7. status
                        Boolean.valueOf(record[8].toString()), // 8. isActive
                        record[9] == null ? null : formatter.parse(record[9].toString()), // 9. createdAt
                        record[10] == null ? null : formatter.parse(record[10].toString()), // 10. updatedAt
                        record[11] == null ? null : formatter.parse(record[11].toString()), // 11. deletedAt
                        Boolean.FALSE, // 12. isDeleted
                        manufacturer // 13. manufacturerId
                );

                toReturn.add(p);
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

    public static Parts getPartsById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartsById");
            spq.registerStoredProcedureParameter("p_parts_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_parts_id", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            // Csak EGY rekord van (getById)
            Object[] record = resultList.get(0);

            // Manufactuer objektum létrehozása
            Manufacturers manufacturer = new Manufacturers();
            manufacturer.setId(Integer.valueOf(record[1].toString()));

            // Parts objektum létrehozása
            Parts p = new Parts(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[2] != null ? record[2].toString() : null, // 2. sku
                    record[3] != null ? record[3].toString() : null, // 3. name
                    record[4] != null ? record[4].toString() : null, // 4. category 
                    record[5] != null ? new BigDecimal(record[5].toString()) : null, // 5. price 
                    record[6] != null ? Integer.valueOf(record[6].toString()) : null, // 6. stock 
                    record[7] != null ? record[7].toString() : null, // 7. status
                    Boolean.valueOf(record[8].toString()), // 8. isActive
                    record[9] == null ? null : formatter.parse(record[9].toString()), // 9. createdAt
                    record[10] == null ? null : formatter.parse(record[10].toString()), // 10. updatedAt
                    record[11] == null ? null : formatter.parse(record[11].toString()), // 11. deletedAt
                    Boolean.FALSE, // 12. isDeleted
                    manufacturer // 13. manufacturerId
            );

            return p;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Parts getPartsByManufacturerId(Integer manufacturerId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartsByManufacturerId");
            spq.registerStoredProcedureParameter("p_manufacturer_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_manufacturer_id", manufacturerId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            // Csak EGY rekord van (getById)
            Object[] record = resultList.get(0);

            // Users objektum létrehozása
            Manufacturers manufacturer = new Manufacturers();
            manufacturer.setId(Integer.valueOf(record[1].toString()));

            // Parts objektum létrehozása
            Parts p = new Parts(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[2] != null ? record[2].toString() : null, // 2. sku
                    record[3] != null ? record[3].toString() : null, // 3. name
                    record[4] != null ? record[4].toString() : null, // 4. category 
                    record[5] != null ? new BigDecimal(record[5].toString()) : null, // 5. price 
                    record[6] != null ? Integer.valueOf(record[6].toString()) : null, // 6. stock 
                    record[7] != null ? record[7].toString() : null, // 7. status
                    Boolean.valueOf(record[8].toString()), // 8. isActive
                    record[9] == null ? null : formatter.parse(record[9].toString()), // 9. createdAt
                    record[10] == null ? null : formatter.parse(record[10].toString()), // 10. updatedAt
                    record[11] == null ? null : formatter.parse(record[11].toString()), // 11. deletedAt
                    Boolean.FALSE, // 12. isDeleted
                    manufacturer // 13. manufacturerId 
            );

            return p;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Boolean softDeleteParts(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteParts");
            spq.registerStoredProcedureParameter("p_parts_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_parts_id", id);

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

    public static ArrayList<String> getPartsCategory() {
        EntityManager em = emf.createEntityManager();
        ArrayList<String> toReturn = new ArrayList<>();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartsCategory");
            spq.execute();

            List<Object> resultList = spq.getResultList();

            for (Object record : resultList) {
                String category = record != null ? record.toString() : null;
                toReturn.add(category);
            }

            return toReturn;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;

        } finally {
            em.close();
        }
    }
    
    public static Boolean updateParts(Parts updatedParts) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateParts");
            
            //Integer id, String sku, String name, String category, BigDecimal price, Integer stock, String status, Boolean isActive, Boolean isDeleted
            
            spq.registerStoredProcedureParameter("p_parts_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_manufacturers_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_sku", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_category", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_price", BigDecimal.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_stock", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_is_active", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_is_deleted", Integer.class, ParameterMode.IN);
            
            spq.setParameter("p_parts_id", updatedParts.getId());
            spq.setParameter("p_manufacturers_id", updatedParts.getManufacturerId().getId());
            spq.setParameter("p_sku", updatedParts.getSku());
            spq.setParameter("p_name", updatedParts.getName());
            spq.setParameter("p_category", updatedParts.getCategory());
            spq.setParameter("p_price", updatedParts.getPrice());
            spq.setParameter("p_stock", updatedParts.getStock());
            spq.setParameter("p_status", updatedParts.getStatus());
            spq.setParameter("p_is_active", Boolean.TRUE.equals(updatedParts.getIsActive()) ? 1 : 0);
            spq.setParameter("p_is_deleted", Boolean.TRUE.equals(updatedParts.getIsDeleted()) ? 1 : 0);

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
    
    public static Parts getPartsBySku(String sku) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartsBySku");
            spq.registerStoredProcedureParameter("p_sku", String.class, ParameterMode.IN);
            spq.setParameter("p_sku", sku);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            // Csak EGY rekord van (getBySku)
            Object[] record = resultList.get(0);

            // Manufactuer objektum létrehozása
            Manufacturers manufacturer = new Manufacturers();
            manufacturer.setId(Integer.valueOf(record[1].toString()));

            // Parts objektum létrehozása
            Parts p = new Parts(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[2] != null ? record[2].toString() : null, // 2. sku
                    record[3] != null ? record[3].toString() : null, // 3. name
                    record[4] != null ? record[4].toString() : null, // 4. category 
                    record[5] != null ? new BigDecimal(record[5].toString()) : null, // 5. price 
                    record[6] != null ? Integer.valueOf(record[6].toString()) : null, // 6. stock 
                    record[7] != null ? record[7].toString() : null, // 7. status
                    Boolean.valueOf(record[8].toString()), // 8. isActive
                    record[9] == null ? null : formatter.parse(record[9].toString()), // 9. createdAt
                    record[10] == null ? null : formatter.parse(record[10].toString()), // 10. updatedAt
                    record[11] == null ? null : formatter.parse(record[11].toString()), // 11. deletedAt
                    Boolean.FALSE, // 12. isDeleted
                    manufacturer // 13. manufacturerId
            );

            return p;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

}
