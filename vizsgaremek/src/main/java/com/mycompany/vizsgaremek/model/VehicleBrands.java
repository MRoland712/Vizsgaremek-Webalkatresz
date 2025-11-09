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
 * @author neblg
 */
@Entity
@Table(name = "vehicle_brands")
@NamedQueries({
    @NamedQuery(name = "VehicleBrands.findAll", query = "SELECT v FROM VehicleBrands v"),
    @NamedQuery(name = "VehicleBrands.findById", query = "SELECT v FROM VehicleBrands v WHERE v.id = :id"),
    @NamedQuery(name = "VehicleBrands.findByName", query = "SELECT v FROM VehicleBrands v WHERE v.name = :name")})
@XmlRootElement
public class VehicleBrands implements Serializable {

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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "brandId")
    private Collection<VehicleModels> vehicleModelsCollection;

    public VehicleBrands() {
    }

    public VehicleBrands(Integer id) {
        this.id = id;
    }

    public VehicleBrands(Integer id, String name) {
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

    @XmlTransient
    public Collection<VehicleModels> getVehicleModelsCollection() {
        return vehicleModelsCollection;
    }

    public void setVehicleModelsCollection(Collection<VehicleModels> vehicleModelsCollection) {
        this.vehicleModelsCollection = vehicleModelsCollection;
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
        if (!(object instanceof VehicleBrands)) {
            return false;
        }
        VehicleBrands other = (VehicleBrands) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.VehicleBrands[ id=" + id + " ]";
    }
    
}
