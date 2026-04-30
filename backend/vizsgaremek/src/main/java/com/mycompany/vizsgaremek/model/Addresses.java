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
@Table(name = "addresses")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Addresses.findAll", query = "SELECT a FROM Addresses a"),
    @NamedQuery(name = "Addresses.findById", query = "SELECT a FROM Addresses a WHERE a.id = :id"),
    @NamedQuery(name = "Addresses.findByFirstName", query = "SELECT a FROM Addresses a WHERE a.firstName = :firstName"),
    @NamedQuery(name = "Addresses.findByLastName", query = "SELECT a FROM Addresses a WHERE a.lastName = :lastName"),
    @NamedQuery(name = "Addresses.findByCompany", query = "SELECT a FROM Addresses a WHERE a.company = :company"),
    @NamedQuery(name = "Addresses.findByTaxNumber", query = "SELECT a FROM Addresses a WHERE a.taxNumber = :taxNumber"),
    @NamedQuery(name = "Addresses.findByCountry", query = "SELECT a FROM Addresses a WHERE a.country = :country"),
    @NamedQuery(name = "Addresses.findByCity", query = "SELECT a FROM Addresses a WHERE a.city = :city"),
    @NamedQuery(name = "Addresses.findByZipCode", query = "SELECT a FROM Addresses a WHERE a.zipCode = :zipCode"),
    @NamedQuery(name = "Addresses.findByStreet", query = "SELECT a FROM Addresses a WHERE a.street = :street"),
    @NamedQuery(name = "Addresses.findByIsDefault", query = "SELECT a FROM Addresses a WHERE a.isDefault = :isDefault"),
    @NamedQuery(name = "Addresses.findByCreatedAt", query = "SELECT a FROM Addresses a WHERE a.createdAt = :createdAt"),
    @NamedQuery(name = "Addresses.findByUpdatedAt", query = "SELECT a FROM Addresses a WHERE a.updatedAt = :updatedAt"),
    @NamedQuery(name = "Addresses.findByIsDeleted", query = "SELECT a FROM Addresses a WHERE a.isDeleted = :isDeleted"),
    @NamedQuery(name = "Addresses.findByDeletedAt", query = "SELECT a FROM Addresses a WHERE a.deletedAt = :deletedAt")})
public class Addresses implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;
    @Size(max = 50)
    @Column(name = "company")
    private String company;
    @Size(max = 50)
    @Column(name = "tax_number")
    private String taxNumber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "country")
    private String country;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "city")
    private String city;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "zip_code")
    private String zipCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "street")
    private String street;
    @Column(name = "is_default")
    private Boolean isDefault;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users userId;
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Addresses() {
    }

    public Addresses(Integer id) {
        this.id = id;
    }

    public Addresses(Integer id, String country, String city, String zipCode, String street) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
    }
    
    //createAddress

    public Addresses(Users userId, String firstName, String lastName, String company, String taxNumber, String country, String city, String zipCode, String street, Boolean isDefault) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.taxNumber = taxNumber;
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.isDefault = isDefault;
    }

    //getAllAddresses & getAddressById & getAddressByUserId
    public Addresses(Integer id, String firstName, String lastName, String company, String taxNumber, String country, String city, String zipCode, String street, Boolean isDefault, Date createdAt, Date updatedAt, Boolean isDeleted, Date deletedAt, Users userId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.taxNumber = taxNumber;
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.userId = userId;
    }
    
    //updateAddress
    public Addresses(Integer id, Users userId,  String firstName, String lastName, String company, String taxNumber, String country, String city, String zipCode, String street, Boolean isDefault, Boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.taxNumber = taxNumber;
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.isDefault = isDefault;
        this.isDeleted = isDeleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
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
        if (!(object instanceof Addresses)) {
            return false;
        }
        Addresses other = (Addresses) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Addresses[ id=" + id + " ]";
    }
    
    public static Boolean createAddress(Addresses createdAddress) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createAddress");

            spq.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_first_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_last_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_company", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_tax_number", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_country", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_city", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_zip_code", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_street", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_is_default", Integer.class, ParameterMode.IN);

            spq.setParameter("p_user_id", createdAddress.getUserId().getId());
            spq.setParameter("p_first_name", createdAddress.getFirstName());
            spq.setParameter("p_last_name", createdAddress.getLastName());
            spq.setParameter("p_company", createdAddress.getCompany());
            spq.setParameter("p_tax_number", createdAddress.getTaxNumber());
            spq.setParameter("p_country", createdAddress.getCountry());
            spq.setParameter("p_city", createdAddress.getCity());
            spq.setParameter("p_zip_code", createdAddress.getZipCode());
            spq.setParameter("p_street", createdAddress.getStreet());
            spq.setParameter("p_is_default", Boolean.TRUE.equals(createdAddress.getIsDefault()) ? 1 : 0);

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static ArrayList<Addresses> getAllAddresses() {
        EntityManager em = emf.createEntityManager();

        try {
            //eljárást meghívjuk
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllAddresses");
            spq.execute();

            //Amit visszakap információt berakni egy Object[] list-be
            List<Object[]> resultList = spq.getResultList();

            ArrayList<Addresses> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                // user_id Users objektum létrehozása
                Users user = new Users();
                user.setId(Integer.valueOf(record[1].toString()));

                Addresses a = new Addresses(
                        Integer.valueOf(record[0].toString()), // 1. id
                        record[2] != null ? record[2].toString() : null, // 2. firstName
                        record[3] != null ? record[3].toString() : null, // 3. lastName
                        record[4] != null ? record[4].toString() : null, // 4. company ← NULL CHECK!
                        record[5] != null ? record[5].toString() : null, // 5. taxNumber
                        record[6] != null ? record[6].toString() : null, // 6. country
                        record[7] != null ? record[7].toString() : null, // 7. city
                        record[8] != null ? record[8].toString() : null, // 8. zipCode
                        record[9] != null ? record[9].toString() : null, // 9. street
                        Boolean.valueOf(record[10].toString()), // 10. isDefault
                        record[11] == null ? null : formatter.parse(record[11].toString()), // 11. createdAt
                        record[12] == null ? null : formatter.parse(record[12].toString()), // 12. updatedAt
                        Boolean.FALSE, // 13. isDeleted
                        null, // 14. deletedAt
                        user // 15. userId
                );

                toReturn.add(a);
            }
            return toReturn;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Ha hiba van, rollback
            }
            ex.printStackTrace();
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }

    public static Addresses getAddressById(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAddressById");
            spq.registerStoredProcedureParameter("p_address_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_address_id", id);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            // Csak EGY rekord van (getById)
            Object[] record = resultList.get(0);

            // Users objektum létrehozása
            Users user = new Users();
            user.setId(Integer.valueOf(record[1].toString()));

            // Addresses objektum létrehozása
            Addresses a = new Addresses(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[2] != null ? record[2].toString() : null, // 2. firstName
                    record[3] != null ? record[3].toString() : null, // 3. lastName
                    record[4] != null ? record[4].toString() : null, // 4. company
                    record[5] != null ? record[5].toString() : null, // 5. taxNumber
                    record[6] != null ? record[6].toString() : null, // 6. country
                    record[7] != null ? record[7].toString() : null, // 7. city
                    record[8] != null ? record[8].toString() : null, // 8. zipCode
                    record[9] != null ? record[9].toString() : null, // 9. street
                    Boolean.valueOf(record[10].toString()), // 10. isDefault
                    record[11] == null ? null : formatter.parse(record[11].toString()), // 11. createdAt
                    record[12] == null ? null : formatter.parse(record[12].toString()), // 12. updatedAt
                    Boolean.FALSE, // 13. isDeleted
                    null, // 14. deletedAt
                    user // 15. userId
            );

            return a;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public static Addresses getAddressByUserId(Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAddressByUserId");
            spq.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_user_id", userId);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            // Csak EGY rekord van (getById)
            Object[] record = resultList.get(0);

            // Users objektum létrehozása
            Users user = new Users();
            user.setId(Integer.valueOf(record[1].toString()));

            // Addresses objektum létrehozása
            Addresses a = new Addresses(
                    Integer.valueOf(record[0].toString()), // 1. id
                    record[2] != null ? record[2].toString() : null, // 2. firstName
                    record[3] != null ? record[3].toString() : null, // 3. lastName
                    record[4] != null ? record[4].toString() : null, // 4. company
                    record[5] != null ? record[5].toString() : null, // 5. taxNumber
                    record[6] != null ? record[6].toString() : null, // 6. country
                    record[7] != null ? record[7].toString() : null, // 7. city
                    record[8] != null ? record[8].toString() : null, // 8. zipCode
                    record[9] != null ? record[9].toString() : null, // 9. street
                    Boolean.valueOf(record[10].toString()), // 10. isDefault
                    record[11] == null ? null : formatter.parse(record[11].toString()), // 11. createdAt
                    record[12] == null ? null : formatter.parse(record[12].toString()), // 12. updatedAt
                    Boolean.FALSE, // 13. isDeleted
                    null, // 14. deletedAt
                    user // 15. userId
            );

            return a;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public static Boolean softDeleteAddress(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteAddress");
            spq.registerStoredProcedureParameter("p_address_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_address_id", id);

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
    
    public static Boolean updateAddress(Addresses updatedAddress) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateAddress");
            
            // Integer id, String firstName, String lastName, String company, String taxNumber, String country, String city, 
            //String zipCode, String street, Boolean isDefault, Boolean isDeleted, Users userId
            
            
            spq.registerStoredProcedureParameter("p_address_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_first_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_last_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_company", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_tax_number", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_country", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_city", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_zip_code", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_street", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_is_default", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_is_deleted", Integer.class, ParameterMode.IN);
            
            spq.setParameter("p_address_id", updatedAddress.getId());
            spq.setParameter("p_user_id", updatedAddress.getUserId().getId());
            spq.setParameter("p_first_name", updatedAddress.getFirstName());
            spq.setParameter("p_last_name", updatedAddress.getLastName());
            spq.setParameter("p_company", updatedAddress.getCompany());
            spq.setParameter("p_tax_number", updatedAddress.getTaxNumber());
            spq.setParameter("p_country", updatedAddress.getCountry());
            spq.setParameter("p_city", updatedAddress.getCity());
            spq.setParameter("p_zip_code", updatedAddress.getZipCode());
            spq.setParameter("p_street", updatedAddress.getStreet());
            spq.setParameter("p_is_default", Boolean.TRUE.equals(updatedAddress.getIsDefault()) ? 1 : 0);
            spq.setParameter("p_is_deleted", Boolean.TRUE.equals(updatedAddress.getIsDeleted()) ? 1 : 0);

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
    
}
