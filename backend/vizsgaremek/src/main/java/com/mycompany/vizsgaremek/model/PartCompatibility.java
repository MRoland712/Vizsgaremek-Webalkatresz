/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author neblgergo
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

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    public PartCompatibility(Integer id, String vehicleType, int vehicleId, Date createdAt, Date updatedAt, Date deletedAt, Boolean isDeleted, Parts partId) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.vehicleId = vehicleId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.isDeleted = isDeleted;
        this.partId = partId;
    }

    public PartCompatibility(String vehicleType, int vehicleId, Parts partId) {
        this.vehicleType = vehicleType;
        this.vehicleId = vehicleId;
        this.partId = partId;
    }

    public PartCompatibility(Integer id, String vehicleType, int vehicleId, Boolean isDeleted, Parts partId) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.vehicleId = vehicleId;
        this.isDeleted = isDeleted;
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

    public static Boolean createPartCompatibility(PartCompatibility createdPartCompatibility) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createPartCompatibility");

            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("vehicleTypeIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("vehicleIdIN", Integer.class, ParameterMode.IN);

            spq.setParameter("partIdIN", createdPartCompatibility.getPartId().getId());
            spq.setParameter("vehicleTypeIN", createdPartCompatibility.getVehicleType());
            spq.setParameter("vehicleIdIN", createdPartCompatibility.getVehicleId());

            spq.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<PartCompatibility> getAllPartCompatibility() {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllPartCompatibility");
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            ArrayList<PartCompatibility> toReturn = new ArrayList<>();

            for (Object[] record : resultList) {
                Parts part = new Parts();
                part.setId(Integer.valueOf(record[1].toString()));

                PartCompatibility pc = new PartCompatibility(
                        Integer.valueOf(record[0].toString()), // id
                        record[2] != null ? record[2].toString() : null, // vehicle_type
                        Integer.valueOf(record[3].toString()), // vehicle_id
                        record[4] == null ? null : formatter.parse(record[4].toString()), // created_at
                        record[5] == null ? null : formatter.parse(record[5].toString()), // updated_at
                        record[7] == null ? null : formatter.parse(record[7].toString()), // deleted_at
                        record[6] != null && Boolean.parseBoolean(record[6].toString()),
                        part
                );
                toReturn.add(pc);
            }
            return toReturn;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static PartCompatibility getPartCompatibilityById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartCompatibilityById");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            Object[] record = resultList.get(0);

            Parts part = new Parts();
            part.setId(Integer.valueOf(record[1].toString()));

            PartCompatibility pc = new PartCompatibility(
                    Integer.valueOf(record[0].toString()),
                    record[2] != null ? record[2].toString() : null,
                    Integer.valueOf(record[3].toString()),
                    record[4] == null ? null : formatter.parse(record[4].toString()),
                    record[5] == null ? null : formatter.parse(record[5].toString()),
                    record[7] == null ? null : formatter.parse(record[7].toString()),
                    record[6] != null && Boolean.parseBoolean(record[6].toString()),
                    part
            );

            return pc;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static ArrayList<PartCompatibility> getPartCompatibilityByVehicleType(String vehicleType) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartCompatibilityByVehicleType");
            spq.registerStoredProcedureParameter("vehicleTypeIN", String.class, ParameterMode.IN);
            spq.setParameter("vehicleTypeIN", vehicleType);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            ArrayList<PartCompatibility> toReturn = new ArrayList<>();

            for (Object[] record : resultList) {
                Parts part = new Parts();
                part.setId(Integer.valueOf(record[1].toString()));

                PartCompatibility pc = new PartCompatibility(
                        Integer.valueOf(record[0].toString()),
                        record[2] != null ? record[2].toString() : null,
                        Integer.valueOf(record[3].toString()),
                        record[4] == null ? null : formatter.parse(record[4].toString()),
                        record[5] == null ? null : formatter.parse(record[5].toString()),
                        record[7] == null ? null : formatter.parse(record[7].toString()),
                        record[6] != null && Boolean.parseBoolean(record[6].toString()),
                        part
                );
                toReturn.add(pc);
            }
            return toReturn;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static ArrayList<PartCompatibility> getPartCompatibilityByVehicleId(Integer vehicleId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getPartCompatibilityByVehicleId");
            spq.registerStoredProcedureParameter("vehicleidIN", Integer.class, ParameterMode.IN);  // ✅ vehicleidIN (kis i!)
            spq.setParameter("vehicleidIN", vehicleId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                return null;
            }

            ArrayList<PartCompatibility> toReturn = new ArrayList<>();

            for (Object[] record : resultList) {
                Parts part = new Parts();
                part.setId(Integer.valueOf(record[1].toString()));

                PartCompatibility pc = new PartCompatibility(
                        Integer.valueOf(record[0].toString()),
                        record[2] != null ? record[2].toString() : null,
                        Integer.valueOf(record[3].toString()),
                        record[4] == null ? null : formatter.parse(record[4].toString()),
                        record[5] == null ? null : formatter.parse(record[5].toString()),
                        record[7] == null ? null : formatter.parse(record[7].toString()),
                        record[6] != null && Boolean.parseBoolean(record[6].toString()),
                        part
                );
                toReturn.add(pc);
            }
            return toReturn;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Boolean softDeletePartCompatibility(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeletePartCompatibility");
            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.setParameter("idIN", id);

            spq.execute();
            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

    public static Boolean updatePartCompatibility(PartCompatibility updatedPartCompatibility) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updatePartCompatibility");

            spq.registerStoredProcedureParameter("idIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("partIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("vehicleTypeIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("vehicleIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("isDeletedIN", Integer.class, ParameterMode.IN);

            spq.setParameter("idIN", updatedPartCompatibility.getId());
            spq.setParameter("partIdIN", updatedPartCompatibility.getPartId().getId());
            spq.setParameter("vehicleTypeIN", updatedPartCompatibility.getVehicleType());
            spq.setParameter("vehicleIdIN", updatedPartCompatibility.getVehicleId());
            spq.setParameter("isDeletedIN", Boolean.TRUE.equals(updatedPartCompatibility.getIsDeleted()) ? 1 : 0);

            spq.execute();
            em.getTransaction().commit();

            return true;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

}
