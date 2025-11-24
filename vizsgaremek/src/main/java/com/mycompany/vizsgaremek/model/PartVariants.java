/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "part_variants")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PartVariants.findAll", query = "SELECT p FROM PartVariants p"),
    @NamedQuery(name = "PartVariants.findById", query = "SELECT p FROM PartVariants p WHERE p.id = :id"),
    @NamedQuery(name = "PartVariants.findByName", query = "SELECT p FROM PartVariants p WHERE p.name = :name"),
    @NamedQuery(name = "PartVariants.findByValue", query = "SELECT p FROM PartVariants p WHERE p.value = :value"),
    @NamedQuery(name = "PartVariants.findByAdditionalPrice", query = "SELECT p FROM PartVariants p WHERE p.additionalPrice = :additionalPrice")})
public class PartVariants implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 100)
    @Column(name = "value")
    private String value;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "additional_price")
    private BigDecimal additionalPrice;
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Parts partId;

    public PartVariants() {
    }

    public PartVariants(Integer id) {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BigDecimal getAdditionalPrice() {
        return additionalPrice;
    }

    public void setAdditionalPrice(BigDecimal additionalPrice) {
        this.additionalPrice = additionalPrice;
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
        if (!(object instanceof PartVariants)) {
            return false;
        }
        PartVariants other = (PartVariants) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.PartVariants[ id=" + id + " ]";
    }
    
}
