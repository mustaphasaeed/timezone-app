package com.sample.backend.timezones.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sample.backend.timezones.def.UserRole;

@Entity
@Table(name = "USERS", uniqueConstraints = @UniqueConstraint(columnNames = { "USERNAME" }) )
public class User implements UserDetails {

    private static final long serialVersionUID = 827385209851381511L;

    private static final long ACCOUNT_EXPIRY_PERIOD = 90;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ACTIVE")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE")
    private UserRole userRole;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "CREATION_DATE")
    @JsonIgnore
    private LocalDateTime creationDate;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "LAST_LOGIN_DATE")
    @JsonIgnore
    private LocalDateTime lastLoginDate;

    public User() {
    }

    public User(String name, String username, String password, UserRole userRole) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.active = true;
        this.lastLoginDate = LocalDateTime.now();
        this.creationDate = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(userRole);
    }

    @Override
    public boolean isAccountNonExpired() {
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(ACCOUNT_EXPIRY_PERIOD);
        return lastLoginDate.isAfter(expiryDate);
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
