/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
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
    @NamedQuery(name = "PartCompatibility.findByEngineType", query = "SELECT p FROM PartCompatibility p WHERE p.engineType = :engineType"),
    @NamedQuery(name = "PartCompatibility.findByTransmission", query = "SELECT p FROM PartCompatibility p WHERE p.transmission = :transmission")})
public class PartCompatibility implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 50)
    @Column(name = "engine_type")
    private String engineType;
    @Size(max = 50)
    @Column(name = "transmission")
    private String transmission;
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Parts partId;
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VehicleModels modelId;

    public PartCompatibility() {
    }

    public PartCompatibility(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Parts getPartId() {
        return partId;
    }

    public void setPartId(Parts partId) {
        this.partId = partId;
    }

    public VehicleModels getModelId() {
        return modelId;
    }

    public void setModelId(VehicleModels modelId) {
        this.modelId = modelId;
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
