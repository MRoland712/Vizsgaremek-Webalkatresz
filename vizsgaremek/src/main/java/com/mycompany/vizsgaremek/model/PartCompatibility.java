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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * @author ddori
 */
@Entity
@Table(name = "part_compatibility")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PartCompatibility.findAll", query = "SELECT p FROM PartCompatibility p"),
    @NamedQuery(name = "PartCompatibility.findById", query = "SELECT p FROM PartCompatibility p WHERE p.id = :id"),
    @NamedQuery(name = "PartCompatibility.findByVehicleType", query = "SELECT p FROM PartCompatibility p WHERE p.vehicleType = :vehicleType"),
    @NamedQuery(name = "PartCompatibility.findByVehicleId", query = "SELECT p FROM PartCompatibility p WHERE p.vehicleId = :vehicleId"),
    @NamedQuery(name = "PartCompatibility.findByCreatedAt", query = "SELECT p FROM PartCompatibility p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "PartCompatibility.findByUpdatedAt", query = "SELECT p FROM PartCompatibility p WHERE p.updatedAt = :updatedAt"),
    @NamedQuery(name = "PartCompatibility.findByDeletedAt", query = "SELECT p FROM PartCompatibility p WHERE p.deletedAt = :deletedAt"),
    @NamedQuery(name = "PartCompatibility.findByIsDeleted", query = "SELECT p FROM PartCompatibility p WHERE p.isDeleted = :isDeleted")})
public class PartCompatibility implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "vehicle_type")
    private String vehicleType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vehicle_id")
    private int vehicleId;
    @Basic(optional = false)
    @NotNull
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
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Parts partId;

    public PartCompatibility() {
    }

    public PartCompatibility(Integer id) {
        this.id = id;
    }

    public PartCompatibility(Integer id, String vehicleType, int vehicleId, Date createdAt) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.vehicleId = vehicleId;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
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
        if (!(object instanceof PartCompatibility)) {
            return false;
        }
        PartCompatibility other = (PartCompatibility) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.PartCompatibility[ id=" + id + " ]";
    }
    
}
