/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ddori
 */
@Entity
@Table(name = "vehicle_models")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VehicleModels.findAll", query = "SELECT v FROM VehicleModels v"),
    @NamedQuery(name = "VehicleModels.findById", query = "SELECT v FROM VehicleModels v WHERE v.id = :id"),
    @NamedQuery(name = "VehicleModels.findByName", query = "SELECT v FROM VehicleModels v WHERE v.name = :name"),
    @NamedQuery(name = "VehicleModels.findByYearFrom", query = "SELECT v FROM VehicleModels v WHERE v.yearFrom = :yearFrom"),
    @NamedQuery(name = "VehicleModels.findByYearTo", query = "SELECT v FROM VehicleModels v WHERE v.yearTo = :yearTo")})
public class VehicleModels implements Serializable {

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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modelId")
    private Collection<PartCompatibility> partCompatibilityCollection;
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VehicleBrands brandId;

    public VehicleModels() {
    }

    public VehicleModels(Integer id) {
        this.id = id;
    }

    public VehicleModels(Integer id, String name) {
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

    @XmlTransient
    public Collection<PartCompatibility> getPartCompatibilityCollection() {
        return partCompatibilityCollection;
    }

    public void setPartCompatibilityCollection(Collection<PartCompatibility> partCompatibilityCollection) {
        this.partCompatibilityCollection = partCompatibilityCollection;
    }

    public VehicleBrands getBrandId() {
        return brandId;
    }

    public void setBrandId(VehicleBrands brandId) {
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
        if (!(object instanceof VehicleModels)) {
            return false;
        }
        VehicleModels other = (VehicleModels) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.VehicleModels[ id=" + id + " ]";
    }
    
}
