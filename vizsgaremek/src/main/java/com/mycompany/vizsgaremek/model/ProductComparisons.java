/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ddori
 */
@Entity
@Table(name = "product_comparisons")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductComparisons.findAll", query = "SELECT p FROM ProductComparisons p"),
    @NamedQuery(name = "ProductComparisons.findById", query = "SELECT p FROM ProductComparisons p WHERE p.id = :id"),
    @NamedQuery(name = "ProductComparisons.findByCreatedAt", query = "SELECT p FROM ProductComparisons p WHERE p.createdAt = :createdAt")})
public class ProductComparisons implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinTable(name = "product_comparison_items", joinColumns = {
        @JoinColumn(name = "comparison_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "part_id", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Parts> partsCollection;

    public ProductComparisons() {
    }

    public ProductComparisons(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @XmlTransient
    public Collection<Parts> getPartsCollection() {
        return partsCollection;
    }

    public void setPartsCollection(Collection<Parts> partsCollection) {
        this.partsCollection = partsCollection;
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
        if (!(object instanceof ProductComparisons)) {
            return false;
        }
        ProductComparisons other = (ProductComparisons) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.ProductComparisons[ id=" + id + " ]";
    }
    
}
