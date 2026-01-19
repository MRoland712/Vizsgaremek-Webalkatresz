/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.mycompany.vizsgaremek.service.AuthenticationService;

/**
 *
 * @author ddori
 */
@Entity
@Table(name = "users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findById", query = "SELECT u FROM Users u WHERE u.id = :id"),
    @NamedQuery(name = "Users.findByEmail", query = "SELECT u FROM Users u WHERE u.email = :email"),
    @NamedQuery(name = "Users.findByUsername", query = "SELECT u FROM Users u WHERE u.username = :username"),
    @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password"),
    @NamedQuery(name = "Users.findByFirstName", query = "SELECT u FROM Users u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "Users.findByLastName", query = "SELECT u FROM Users u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "Users.findByPhone", query = "SELECT u FROM Users u WHERE u.phone = :phone"),
    @NamedQuery(name = "Users.findByIsActive", query = "SELECT u FROM Users u WHERE u.isActive = :isActive"),
    @NamedQuery(name = "Users.findByRole", query = "SELECT u FROM Users u WHERE u.role = :role"),
    @NamedQuery(name = "Users.findByCreatedAt", query = "SELECT u FROM Users u WHERE u.createdAt = :createdAt"),
    @NamedQuery(name = "Users.findByUpdatedAt", query = "SELECT u FROM Users u WHERE u.updatedAt = :updatedAt"),
    @NamedQuery(name = "Users.findByLastLogin", query = "SELECT u FROM Users u WHERE u.lastLogin = :lastLogin"),
    @NamedQuery(name = "Users.findByFailedLoginAttempts", query = "SELECT u FROM Users u WHERE u.failedLoginAttempts = :failedLoginAttempts"),
    @NamedQuery(name = "Users.findByLockedUntil", query = "SELECT u FROM Users u WHERE u.lockedUntil = :lockedUntil"),
    @NamedQuery(name = "Users.findByTimezone", query = "SELECT u FROM Users u WHERE u.timezone = :timezone"),
    @NamedQuery(name = "Users.findByEmailVerified", query = "SELECT u FROM Users u WHERE u.emailVerified = :emailVerified"),
    @NamedQuery(name = "Users.findByPhoneVerified", query = "SELECT u FROM Users u WHERE u.phoneVerified = :phoneVerified"),
    @NamedQuery(name = "Users.findByIsSubscribed", query = "SELECT u FROM Users u WHERE u.isSubscribed = :isSubscribed"),
    @NamedQuery(name = "Users.findByDeletedAt", query = "SELECT u FROM Users u WHERE u.deletedAt = :deletedAt"),
    @NamedQuery(name = "Users.findByIsDeleted", query = "SELECT u FROM Users u WHERE u.isDeleted = :isDeleted"),
    @NamedQuery(name = "Users.findByAuthSecret", query = "SELECT u FROM Users u WHERE u.authSecret = :authSecret"),
    @NamedQuery(name = "Users.findByGuid", query = "SELECT u FROM Users u WHERE u.guid = :guid"),
    @NamedQuery(name = "Users.findByRegistrationToken", query = "SELECT u FROM Users u WHERE u.registrationToken = :registrationToken")})
public class Users implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<CartItems> cartItemsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<Reviews> reviewsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<Orders> ordersCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;
    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "phone")
    private String phone;
    @Column(name = "is_active")
    private Boolean isActive;
    @Size(max = 20)
    @Column(name = "role")
    private String role;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;
    @Column(name = "locked_until")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lockedUntil;
    @Size(max = 50)
    @Column(name = "timezone")
    private String timezone;
    @Column(name = "email_verified")
    private Boolean emailVerified;
    @Column(name = "phone_verified")
    private Boolean phoneVerified;
    @Column(name = "is_subscribed")
    private Boolean isSubscribed;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "auth_secret")
    private String authSecret;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 36)
    @Column(name = "guid")
    private String guid;
    @Size(max = 255)
    @Column(name = "registration_token")
    private String registrationToken;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Collection<UserLogs> userLogsCollection;

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static AuthenticationService.userAuth userAuth = new AuthenticationService.userAuth();

    public Users() {
    }

    //loginUser
    public Users(String email, String password) {
        this.email = email;
        this.password = password;
    }

    //UpdateUser
    public Users(Integer id, String email, String username, String firstName, String lastName, String phone, Boolean isActive, String role, String password, String authSecret, String registrationToken) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.isActive = isActive;
        this.role = role;
        this.password = password;
        this.authSecret = authSecret;
        this.registrationToken = registrationToken;
    }

    //createUser
    public Users(String email, String username, String password, String firstName, String lastName, String phone, String role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
    }

    //getUser
    public Users(
            Integer id,
            String email,
            String username,
            String firstName,
            String lastName,
            String phone,
            String guid,
            String role,
            Boolean isActive,
            Date lastLogin,
            Date createdAt,
            Date updatedAt,
            Boolean isDeleted,
            Boolean isSubscribed) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.isActive = isActive;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLogin = lastLogin;
        this.guid = guid;
        this.isDeleted = isDeleted;
        this.isSubscribed = isSubscribed;
    }

    //getUserById && getUserByEmail
    public Users(
            Integer id,
            String email,
            String username,
            String firstName,
            String lastName,
            String phone,
            String guid,
            String role,
            Boolean isActive,
            Boolean isSubscribed,
            Date lastLogin,
            Date createdAt,
            Date updatedAt,
            String password,
            Boolean isDeleted,
            String authSecret,
            String registrationToken) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.isActive = isActive;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLogin = lastLogin;
        this.isDeleted = isDeleted;
        this.authSecret = authSecret;
        this.guid = guid;
        this.registrationToken = registrationToken;
        this.isSubscribed = isSubscribed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Date getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(Date lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Boolean getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(Boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
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

    public String getAuthSecret() {
        return authSecret;
    }

    public void setAuthSecret(String authSecret) {
        this.authSecret = authSecret;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    @XmlTransient
    public Collection<UserLogs> getUserLogsCollection() {
        return userLogsCollection;
    }

    public void setUserLogsCollection(Collection<UserLogs> userLogsCollection) {
        this.userLogsCollection = userLogsCollection;
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
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Users[ id=" + id + " ]";
    }

    public static Boolean createUser(Users createdUser) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("createUser");

            spq.registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_username", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_password", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_first_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_last_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_phone", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_role", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_auth_secret", String.class, ParameterMode.IN); //OTP
            spq.registerStoredProcedureParameter("p_registration_token", String.class, ParameterMode.IN); //JWT

            spq.setParameter("p_email", createdUser.getEmail());
            spq.setParameter("p_username", createdUser.getUsername());
            spq.setParameter("p_password", createdUser.getPassword());
            spq.setParameter("p_first_name", createdUser.getFirstName());
            spq.setParameter("p_last_name", createdUser.getLastName());
            spq.setParameter("p_phone", createdUser.getPhone());
            spq.setParameter("p_role", createdUser.getRole() == null ? "" : createdUser.getRole());
            spq.setParameter("p_auth_secret", createdUser.getAuthSecret());
            spq.setParameter("p_registration_token", createdUser.getRegistrationToken());

            spq.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

    public static ArrayList<Users> getUsers() {
        EntityManager em = emf.createEntityManager();

        try {
            //eljárást meghívjuk
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getUsers");
            spq.execute();

            //Amit visszakap információt berakni egy Object[] list-be
            List<Object[]> resultList = spq.getResultList();

            ArrayList<Users> toReturn = new ArrayList();

            for (Object[] record : resultList) {
                Users u = new Users(
                        Integer.valueOf(record[0].toString()), //Id
                        record[1].toString(), // email
                        record[2].toString(), // username
                        record[3].toString(), // firstname
                        record[4].toString(), // lastname
                        record[5].toString(), // phone
                        record[6].toString(), // guid
                        record[7].toString(), // role
                        Boolean.valueOf(record[8].toString()), // is_active
                        record[9] == null ? null : formatter.parse(record[9].toString()), // last_login
                        record[10] == null ? null : formatter.parse(record[10].toString()), // created_at
                        record[11] == null ? null : formatter.parse(record[11].toString()), // updated_at
                        Boolean.valueOf(record[12].toString()), // is_deleted
                        Boolean.valueOf(record[13].toString()) // is_subscibed
                );
                toReturn.add(u);

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

    public static Users getUserById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getUserById");
            spq.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_user_id", id);

            spq.execute(); // vége zárásként

            List<Object[]> resultList = spq.getResultList();

            if (userAuth.isDataMissing(resultList)) {
                return null;
            }

            Users toReturn = new Users();

            for (Object[] record : resultList) {

                Users u = new Users(
                        Integer.valueOf(record[0].toString()),// id
                        record[1].toString(),// email
                        record[2].toString(),// username
                        record[3].toString(),// firstname
                        record[4].toString(),// lastname
                        record[5].toString(),// phone
                        record[6].toString(),// guid
                        record[7].toString(),// role
                        Boolean.valueOf(record[8].toString()),// isActive
                        Boolean.valueOf(record[9].toString()), // isSubscibed
                        record[10] == null ? null : formatter.parse(record[10].toString()),// lastLogin
                        record[11] == null ? null : formatter.parse(record[11].toString()),// createdAt
                        record[12] == null ? null : formatter.parse(record[12].toString()),// updatedAt
                        record[13].toString(),// password
                        Boolean.valueOf(record[14].toString()),// isDeleted
                        record[15].toString(),// authSecret
                        record[16].toString()// registrationToken
                );
                toReturn = u;
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

    public static Users getUserByEmail(String email) {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getUserByEmail");
            spq.registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN);
            spq.setParameter("p_email", email);

            spq.execute();

            List<Object[]> resultList = spq.getResultList();

            if (userAuth.isDataMissing(resultList)) {
                return null;
            }

            Users toReturn = new Users();
            for (Object[] record : resultList) {
                Users u = new Users(
                        Integer.valueOf(record[0].toString()),// id
                        record[1].toString(),// email
                        record[2].toString(),// username
                        record[3].toString(),// firstname
                        record[4].toString(),// lastname
                        record[5].toString(),// phone
                        record[6].toString(),// guid
                        record[7].toString(),// role
                        Boolean.valueOf(record[8].toString()),// isActive
                        Boolean.valueOf(record[9].toString()), // isSubscibed
                        record[10] == null ? null : formatter.parse(record[10].toString()),// lastLogin
                        record[11] == null ? null : formatter.parse(record[11].toString()),// createdAt
                        record[12] == null ? null : formatter.parse(record[12].toString()),// updatedAt
                        record[13].toString(),// password
                        Boolean.valueOf(record[14].toString()),// isDeleted
                        record[15].toString(),// authSecret
                        record[16].toString()// registrationToken

                );
                toReturn = u;
            }
            return toReturn;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Ha hiba van, rollback
            }
            ex.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Boolean softDeleteUser(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            StoredProcedureQuery spq = em.createStoredProcedureQuery("softDeleteUser");
            spq.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            spq.setParameter("p_user_id", id);

            spq.execute();

            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // ha hiba van, rollback
            }
            System.err.println(e);
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

    public static Boolean updateUser(Users updatedUser) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateUser");

            spq.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_username", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_first_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_last_name", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_phone", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_role", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_is_active", Boolean.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_password", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_registration_token", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_auth_secret", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("p_is_subscribed", Boolean.class, ParameterMode.IN);

            spq.setParameter("p_user_id", updatedUser.getId());
            spq.setParameter("p_email", updatedUser.getEmail());
            spq.setParameter("p_username", updatedUser.getUsername());
            spq.setParameter("p_first_name", updatedUser.getFirstName());
            spq.setParameter("p_last_name", updatedUser.getLastName());
            spq.setParameter("p_phone", updatedUser.getPhone());
            spq.setParameter("p_role", updatedUser.getRole());
            spq.setParameter("p_is_active", updatedUser.getIsActive());
            spq.setParameter("p_password", updatedUser.getPassword());
            spq.setParameter("p_registration_token", updatedUser.getRegistrationToken());
            spq.setParameter("p_auth_secret", updatedUser.getAuthSecret());
            spq.setParameter("p_is_subscribed", updatedUser.getIsSubscribed());

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

    public static Boolean loginUser(Users userData) {
        EntityManager em = emf.createEntityManager();
        try {

            StoredProcedureQuery spq = em.createStoredProcedureQuery("user_login");

            spq.registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN);

            spq.setParameter("p_email", userData.getEmail());

            spq.execute();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }
    
    public static Boolean loginAdmin(Users userData) {
        EntityManager em = emf.createEntityManager();
        try {

            StoredProcedureQuery spq = em.createStoredProcedureQuery("user_login");

            spq.registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN);

            spq.setParameter("p_email", userData.getEmail());

            spq.execute();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }

    @XmlTransient
    public Collection<CartItems> getCartItemsCollection() {
        return cartItemsCollection;
    }

    public void setCartItemsCollection(Collection<CartItems> cartItemsCollection) {
        this.cartItemsCollection = cartItemsCollection;
    }

    @XmlTransient
    public Collection<Reviews> getReviewsCollection() {
        return reviewsCollection;
    }

    public void setReviewsCollection(Collection<Reviews> reviewsCollection) {
        this.reviewsCollection = reviewsCollection;
    }

    @XmlTransient
    public Collection<Orders> getOrdersCollection() {
        return ordersCollection;
    }

    public void setOrdersCollection(Collection<Orders> ordersCollection) {
        this.ordersCollection = ordersCollection;
    }
}
