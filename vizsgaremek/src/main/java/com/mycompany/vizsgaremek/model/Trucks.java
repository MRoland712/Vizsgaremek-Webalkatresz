/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author neblg
 */
@Entity
@Table(name = "trucks")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Trucks.findAll", query = "SELECT t FROM Trucks t"),
    @NamedQuery(name = "Trucks.findById", query = "SELECT t FROM Trucks t WHERE t.id = :id"),
    @NamedQuery(name = "Trucks.findByBrand", query = "SELECT t FROM Trucks t WHERE t.brand = :brand"),
    @NamedQuery(name = "Trucks.findByModel", query = "SELECT t FROM Trucks t WHERE t.model = :model"),
    @NamedQuery(name = "Trucks.findByYearFrom", query = "SELECT t FROM Trucks t WHERE t.yearFrom = :yearFrom"),
    @NamedQuery(name = "Trucks.findByYearTo", query = "SELECT t FROM Trucks t WHERE t.yearTo = :yearTo"),
    @NamedQuery(name = "Trucks.findByCreatedAt", query = "SELECT t FROM Trucks t WHERE t.createdAt = :createdAt"),
    @NamedQuery(name = "Trucks.findByUpdatedAt", query = "SELECT t FROM Trucks t WHERE t.updatedAt = :updatedAt"),
    @NamedQuery(name = "Trucks.findByIsDeleted", query = "SELECT t FROM Trucks t WHERE t.isDeleted = :isDeleted"),
    @NamedQuery(name = "Trucks.findByDeletedAt", query = "SELECT t FROM Trucks t WHERE t.deletedAt = :deletedAt")})
public class Trucks implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "brand")
    private String brand;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "model")
    private String model;
    @Column(name = "year_from")
    private Integer yearFrom;
    @Column(name = "year_to")
    private Integer yearTo;
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

    public Trucks() {
    }

    public Trucks(Integer id) {
        this.id = id;
    }

    public Trucks(Integer id, String brand, String model) {
        this.id = id;
        this.brand = brand;
        this.model = model;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Trucks)) {
            return false;
        }
        Trucks other = (Trucks) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Trucks[ id=" + id + " ]";
    }
    
}
