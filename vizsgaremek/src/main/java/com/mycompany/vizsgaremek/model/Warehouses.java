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

/**
 *
 * @author neblg
 */
@Entity
@Table(name = "warehouses")
@NamedQueries({
    @NamedQuery(name = "Warehouses.findAll", query = "SELECT w FROM Warehouses w"),
    @NamedQuery(name = "Warehouses.findById", query = "SELECT w FROM Warehouses w WHERE w.id = :id"),
    @NamedQuery(name = "Warehouses.findByName", query = "SELECT w FROM Warehouses w WHERE w.name = :name"),
    @NamedQuery(name = "Warehouses.findByLocation", query = "SELECT w FROM Warehouses w WHERE w.location = :location"),
    @NamedQuery(name = "Warehouses.findByCreatedAt", query = "SELECT w FROM Warehouses w WHERE w.createdAt = :createdAt"),
    @NamedQuery(name = "Warehouses.findByIsDeleted", query = "SELECT w FROM Warehouses w WHERE w.isDeleted = :isDeleted"),
    @NamedQuery(name = "Warehouses.findByDeletedAt", query = "SELECT w FROM Warehouses w WHERE w.deletedAt = :deletedAt")})
public class Warehouses implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "location")
    private String location;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    public Warehouses() {
    }

    public Warehouses(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Warehouses)) {
            return false;
        }
        Warehouses other = (Warehouses) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Warehouses[ id=" + id + " ]";
    }
    
}
