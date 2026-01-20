/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import static com.mycompany.vizsgaremek.model.Users.emf;
import com.mycompany.vizsgaremek.service.AuthenticationService;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 * @author ddori
 */
@Entity
@Table(name = "users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users_1.findAll", query = "SELECT u FROM Users_1 u"),
    @NamedQuery(name = "Users_1.findById", query = "SELECT u FROM Users_1 u WHERE u.id = :id"),
    @NamedQuery(name = "Users_1.findByEmail", query = "SELECT u FROM Users_1 u WHERE u.email = :email"),
    @NamedQuery(name = "Users_1.findByUsername", query = "SELECT u FROM Users_1 u WHERE u.username = :username"),
    @NamedQuery(name = "Users_1.findByPassword", query = "SELECT u FROM Users_1 u WHERE u.password = :password"),
    @NamedQuery(name = "Users_1.findByFirstName", query = "SELECT u FROM Users_1 u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "Users_1.findByLastName", query = "SELECT u FROM Users_1 u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "Users_1.findByPhone", query = "SELECT u FROM Users_1 u WHERE u.phone = :phone"),
    @NamedQuery(name = "Users_1.findByIsActive", query = "SELECT u FROM Users_1 u WHERE u.isActive = :isActive"),
    @NamedQuery(name = "Users_1.findByRole", query = "SELECT u FROM Users_1 u WHERE u.role = :role"),
    @NamedQuery(name = "Users_1.findByCreatedAt", query = "SELECT u FROM Users_1 u WHERE u.createdAt = :createdAt"),
    @NamedQuery(name = "Users_1.findByUpdatedAt", query = "SELECT u FROM Users_1 u WHERE u.updatedAt = :updatedAt"),
    @NamedQuery(name = "Users_1.findByLastLogin", query = "SELECT u FROM Users_1 u WHERE u.lastLogin = :lastLogin"),
    @NamedQuery(name = "Users_1.findByFailedLoginAttempts", query = "SELECT u FROM Users_1 u WHERE u.failedLoginAttempts = :failedLoginAttempts"),
    @NamedQuery(name = "Users_1.findByLockedUntil", query = "SELECT u FROM Users_1 u WHERE u.lockedUntil = :lockedUntil"),
    @NamedQuery(name = "Users_1.findByTimezone", query = "SELECT u FROM Users_1 u WHERE u.timezone = :timezone"),
    @NamedQuery(name = "Users_1.findByEmailVerified", query = "SELECT u FROM Users_1 u WHERE u.emailVerified = :emailVerified"),
    @NamedQuery(name = "Users_1.findByPhoneVerified", query = "SELECT u FROM Users_1 u WHERE u.phoneVerified = :phoneVerified"),
    @NamedQuery(name = "Users_1.findByIsSubscribed", query = "SELECT u FROM Users_1 u WHERE u.isSubscribed = :isSubscribed"),
    @NamedQuery(name = "Users_1.findByDeletedAt", query = "SELECT u FROM Users_1 u WHERE u.deletedAt = :deletedAt"),
    @NamedQuery(name = "Users_1.findByIsDeleted", query = "SELECT u FROM Users_1 u WHERE u.isDeleted = :isDeleted"),
    @NamedQuery(name = "Users_1.findByAuthSecret", query = "SELECT u FROM Users_1 u WHERE u.authSecret = :authSecret"),
    @NamedQuery(name = "Users_1.findByGuid", query = "SELECT u FROM Users_1 u WHERE u.guid = :guid"),
    @NamedQuery(name = "Users_1.findByRegistrationToken", query = "SELECT u FROM Users_1 u WHERE u.registrationToken = :registrationToken")})
public class Users_1 implements Serializable {

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

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU");
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static AuthenticationService.userAuth userAuth = new AuthenticationService.userAuth();
    
    public Users_1() {
    }

    public Users_1(Integer id) {
        this.id = id;
    }

    public Users_1(Integer id, String email, String username, String password, String authSecret, String guid) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.authSecret = authSecret;
        this.guid = guid;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users_1)) {
            return false;
        }
        Users_1 other = (Users_1) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Users_1[ id=" + id + " ]";
    }
    
    public static Boolean loginAdmin(Users userData) {
        EntityManager em = emf.createEntityManager();
        try {

            StoredProcedureQuery spq = em.createStoredProcedureQuery("admin_login");

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
}
