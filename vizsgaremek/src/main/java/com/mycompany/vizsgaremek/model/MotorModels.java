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
@Table(name = "motor_models")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MotorModels.findAll", query = "SELECT m FROM MotorModels m"),
    @NamedQuery(name = "MotorModels.findById", query = "SELECT m FROM MotorModels m WHERE m.id = :id"),
    @NamedQuery(name = "MotorModels.findByName", query = "SELECT m FROM MotorModels m WHERE m.name = :name"),
    @NamedQuery(name = "MotorModels.findByYearFrom", query = "SELECT m FROM MotorModels m WHERE m.yearFrom = :yearFrom"),
    @NamedQuery(name = "MotorModels.findByYearTo", query = "SELECT m FROM MotorModels m WHERE m.yearTo = :yearTo"),
    @NamedQuery(name = "MotorModels.findByCreatedAt", query = "SELECT m FROM MotorModels m WHERE m.createdAt = :createdAt")})
public class MotorModels implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @Column(name = "year_from")
    private Integer yearFrom;
    @Column(name = "year_to")
    private Integer yearTo;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private MotorBrands brandId;

    public MotorModels() {
    }

    public MotorModels(Integer id) {
        this.id = id;
    }

    public MotorModels(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public MotorBrands getBrandId() {
        return brandId;
    }

    public void setBrandId(MotorBrands brandId) {
        this.brandId = brandId;
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
        if (!(object instanceof MotorModels)) {
            return false;
        }
        MotorModels other = (MotorModels) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.MotorModels[ id=" + id + " ]";
    }
    
}
