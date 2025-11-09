/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
    @NamedQuery(name = "Parts.findByDeletedAt", query = "SELECT p FROM Parts p WHERE p.deletedAt = :deletedAt")})
@XmlRootElement
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
    @ManyToMany(mappedBy = "partsCollection")
    private Collection<ProductComparisons> productComparisonsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<PartCompatibility> partCompatibilityCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<Reviews> reviewsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<StockLogs> stockLogsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<OrderItems> orderItemsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<CartItems> cartItemsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<PartImages> partImagesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<PartVariants> partVariantsCollection;
    @JoinColumn(name = "manufacturer_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Manufacturers manufacturerId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partId")
    private Collection<WarehouseStock> warehouseStockCollection;

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

    @XmlTransient
    public Collection<ProductComparisons> getProductComparisonsCollection() {
        return productComparisonsCollection;
    }

    public void setProductComparisonsCollection(Collection<ProductComparisons> productComparisonsCollection) {
        this.productComparisonsCollection = productComparisonsCollection;
    }

    @XmlTransient
    public Collection<PartCompatibility> getPartCompatibilityCollection() {
        return partCompatibilityCollection;
    }

    public void setPartCompatibilityCollection(Collection<PartCompatibility> partCompatibilityCollection) {
        this.partCompatibilityCollection = partCompatibilityCollection;
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

    @XmlTransient
    public Collection<OrderItems> getOrderItemsCollection() {
        return orderItemsCollection;
    }

    public void setOrderItemsCollection(Collection<OrderItems> orderItemsCollection) {
        this.orderItemsCollection = orderItemsCollection;
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
    
}
