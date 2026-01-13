/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
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
@Table(name = "part_variants")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PartVariants.findAll", query = "SELECT p FROM PartVariants p"),
    @NamedQuery(name = "PartVariants.findById", query = "SELECT p FROM PartVariants p WHERE p.id = :id"),
    @NamedQuery(name = "PartVariants.findByName", query = "SELECT p FROM PartVariants p WHERE p.name = :name"),
    @NamedQuery(name = "PartVariants.findByValue", query = "SELECT p FROM PartVariants p WHERE p.value = :value"),
    @NamedQuery(name = "PartVariants.findByAdditionalPrice", query = "SELECT p FROM PartVariants p WHERE p.additionalPrice = :additionalPrice"),
    @NamedQuery(name = "PartVariants.findByCreatedAt", query = "SELECT p FROM PartVariants p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "PartVariants.findByIsDeleted", query = "SELECT p FROM PartVariants p WHERE p.isDeleted = :isDeleted"),
    @NamedQuery(name = "PartVariants.findByDeletedAt", query = "SELECT p FROM PartVariants p WHERE p.deletedAt = :deletedAt")})
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
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Parts partId;

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    public Parts getPartId() {
        return partId;
    }

    public void setPartId(Parts partId) {
        this.partId = partId;
    }

    //createPartVariants
    public PartVariants(Parts partId, String name, String value, BigDecimal additionalPrice) {
        this.partId = partId;
        this.name = name;
        this.value = value;
        this.additionalPrice = additionalPrice;
    }

    //getAllPartVaraints
    public PartVariants(Integer id, String name, String value, BigDecimal additionalPrice, Date createdAt, Boolean isDeleted, Date deletedAt, Parts partId) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.additionalPrice = additionalPrice;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
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

    public static Boolean createPartVariants(PartVariants createdPartVariants) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createPartVariants");

            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("valueIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("additionalPriceIN", BigDecimal.class, ParameterMode.IN);

            spq.setParameter("partIdIN", createdPartVariants.getPartId().getId());
            spq.setParameter("nameIN", createdPartVariants.getName());
            spq.setParameter("valueIN", createdPartVariants.getValue());
            spq.setParameter("additionalPriceIN", createdPartVariants.getAdditionalPrice());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<PartVariants> getAllPartVariants() {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllPartVariants");
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            ArrayList<PartVariants> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                // part_id Parts objektum létrehozása
                Parts part = new Parts();
                part.setId(Integer.valueOf(record[1].toString()));

                PartVariants pv = new PartVariants(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] != null ? record[2].toString() : null, // 2. name
                        record[3] != null ? record[3].toString() : null, // 3. value
                        record[4] != null ? new BigDecimal(record[4].toString()) : null, // 4. additionalPrice 
                        record[5] == null ? null : formatter.parse(record[5].toString()), //  createdAt
                        Boolean.valueOf(record[6].toString()),
                        record[7] == null ? null : formatter.parse(record[6].toString()), //  deletedAt
                        part // 13 partId
                );

                toReturn.add(pv);
            }
            return toReturn;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }

    public static PartVariants getPartVariantsById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartVariantsById");
            spq.registerStoredProcedureParameter("partVariantsId", Integer.class, ParameterMode.IN);
            spq.setParameter("partVariantsId", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            Object[] record = resultList.get(0);

            Parts part = new Parts();
            part.setId(Integer.valueOf(record[1].toString()));

            PartVariants pv = new PartVariants(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[2] != null ? record[2].toString() : null, // 2. name
                    record[3] != null ? record[3].toString() : null, // 3. value
                    record[4] != null ? new BigDecimal(record[4].toString()) : null, // 4. additionalPrice 
                    record[5] == null ? null : formatter.parse(record[5].toString()), //  createdAt
                    Boolean.valueOf(record[6].toString()),
                    record[7] == null ? null : formatter.parse(record[6].toString()), //  deletedAt
                    part //  partId
            );

            return pv;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Boolean softDeletePartVariants(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeletePartVariants");
            spq.registerStoredProcedureParameter("partVaraintsId", Integer.class, ParameterMode.IN);
            spq.setParameter("partVaraintsId", id);

            spq.execute();
            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // ha hiba van, rollback
            }
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

    public static Boolean updatePartVariants(PartVariants updatedPartVariants) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updatePartVariants");

            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("valueIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("additionalPriceIN", BigDecimal.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isDeletedIN", Integer.class, ParameterMode.IN);

            spq.setParameter("idIN", updatedPartVariants.getId());
            spq.setParameter("partIdIN", updatedPartVariants.getPartId().getId());
            spq.setParameter("nameIN", updatedPartVariants.getName());
            spq.setParameter("valueIN", updatedPartVariants.getValue());
            spq.setParameter("additionalPriceIN", updatedPartVariants.getAdditionalPrice());
            spq.setParameter("isDeletedIN", Boolean.TRUE.equals(updatedPartVariants.getIsDeleted()) ? 1 : 0);

            spq.execute();

            em.getTransaction().commit();

            return true;

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Ha hiba van, rollback
            }
            ex.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

    public static ArrayList<PartVariants> getPartVariantsByName(String name) {
        EntityManager em = emf.createEntityManager();
        ArrayList<PartVariants> toReturn = new ArrayList<>();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartVariantsByName");

            spq.registerStoredProcedureParameter("partVariantsName", String.class, ParameterMode.IN);
            spq.setParameter("partVariantsName", name);

            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            
            for (Object[] record : resultList) {
                // Parts objektum
                Parts part = new Parts();
                part.setId(Integer.valueOf(record[1].toString()));

                // PartVariants objektum
                PartVariants pv = new PartVariants(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] != null ? record[2].toString() : null, // 2. name
                        record[3] != null ? record[3].toString() : null, // 3. value
                        record[4] != null ? new BigDecimal(record[4].toString()) : null, // 4. additional_price
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 5. created_at
                        Boolean.valueOf(record[6].toString()), // 6. is_deleted
                        record[7] == null ? null : formatter.parse(record[7].toString()), // 7. deleted_at (JAVÍTVA! volt: record[6])
                        part // 8. partId
                );

                toReturn.add(pv);  // ← Hozzáadjuk a listához!
            }

            return toReturn;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;

        } finally {
            em.close();
        }
    }

    public static ArrayList<PartVariants> getPartVariantsByValue(String value) {
        EntityManager em = emf.createEntityManager();
        ArrayList<PartVariants> toReturn = new ArrayList<>();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartVariantsByValue");

            spq.registerStoredProcedureParameter("partVariantsValue", String.class, ParameterMode.IN);
            spq.setParameter("partVariantsValue", value);

            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            
            for (Object[] record : resultList) {
                // Parts objektum
                Parts part = new Parts();
                part.setId(Integer.valueOf(record[1].toString()));

                // PartVariants objektum
                PartVariants pv = new PartVariants(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] != null ? record[2].toString() : null, // 2. name
                        record[3] != null ? record[3].toString() : null, // 3. value
                        record[4] != null ? new BigDecimal(record[4].toString()) : null, // 4. additional_price
                        record[5] == null ? null : formatter.parse(record[5].toString()), // 5. created_at
                        Boolean.valueOf(record[6].toString()), // 6. is_deleted
                        record[7] == null ? null : formatter.parse(record[7].toString()), // 7. deleted_at
                        part // 8. partId
                );

                toReturn.add(pv);  
            }

            return toReturn;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;

        } finally {
            em.close();
        }
    }

}
