package com.sample.backend.timezones.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "TIMEZONES")
public class Timezone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CITY")
    private String city;

    @Column(name = "OFF_SET")
    private int offset;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public Timezone() {

    }

    public Timezone(String name, String city, int offset, User user) {
        this.name = name;
        this.city = city;
        this.offset = offset;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public int getOffset() {
        return offset;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public String getOwner() {
        return user.getName();
    }

}
