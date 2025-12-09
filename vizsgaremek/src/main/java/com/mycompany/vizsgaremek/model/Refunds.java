/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * @author neblg
 */
@Entity
@Table(name = "refunds")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Refunds.findAll", query = "SELECT r FROM Refunds r"),
    @NamedQuery(name = "Refunds.findById", query = "SELECT r FROM Refunds r WHERE r.id = :id"),
    @NamedQuery(name = "Refunds.findByAmount", query = "SELECT r FROM Refunds r WHERE r.amount = :amount"),
    @NamedQuery(name = "Refunds.findByReason", query = "SELECT r FROM Refunds r WHERE r.reason = :reason"),
    @NamedQuery(name = "Refunds.findByRefundedAt", query = "SELECT r FROM Refunds r WHERE r.refundedAt = :refundedAt"),
    @NamedQuery(name = "Refunds.findByIsDeleted", query = "SELECT r FROM Refunds r WHERE r.isDeleted = :isDeleted"),
    @NamedQuery(name = "Refunds.findByDeletedAt", query = "SELECT r FROM Refunds r WHERE r.deletedAt = :deletedAt")})
public class Refunds implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "amount")
    private BigDecimal amount;
    @Size(max = 255)
    @Column(name = "reason")
    private String reason;
    @Column(name = "refunded_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date refundedAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Payments paymentId;

    public Refunds() {
    }

    public Refunds(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(Date refundedAt) {
        this.refundedAt = refundedAt;
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

    public Payments getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Payments paymentId) {
        this.paymentId = paymentId;
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
        if (!(object instanceof Refunds)) {
            return false;
        }
        Refunds other = (Refunds) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Refunds[ id=" + id + " ]";
    }
    
}
