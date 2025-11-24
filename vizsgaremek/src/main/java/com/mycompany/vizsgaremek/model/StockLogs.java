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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ddori
 */
@Entity
@Table(name = "stock_logs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StockLogs.findAll", query = "SELECT s FROM StockLogs s"),
    @NamedQuery(name = "StockLogs.findById", query = "SELECT s FROM StockLogs s WHERE s.id = :id"),
    @NamedQuery(name = "StockLogs.findByChangeAmount", query = "SELECT s FROM StockLogs s WHERE s.changeAmount = :changeAmount"),
    @NamedQuery(name = "StockLogs.findByReason", query = "SELECT s FROM StockLogs s WHERE s.reason = :reason"),
    @NamedQuery(name = "StockLogs.findByCreatedAt", query = "SELECT s FROM StockLogs s WHERE s.createdAt = :createdAt")})
public class StockLogs implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "change_amount")
    private Integer changeAmount;
    @Size(max = 255)
    @Column(name = "reason")
    private String reason;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Parts partId;

    public StockLogs() {
    }

    public StockLogs(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Integer changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
        if (!(object instanceof StockLogs)) {
            return false;
        }
        StockLogs other = (StockLogs) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.StockLogs[ id=" + id + " ]";
    }
    
}
