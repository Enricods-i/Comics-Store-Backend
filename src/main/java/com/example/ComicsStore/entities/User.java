package com.example.ComicsStore.entities;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "personal_data", schema = "public")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private BigInteger id;

    @Basic
    @Column(name = "first_name", nullable = false, length = 50)
    private String first_name;

    @Basic
    @Column(name = "last_name", nullable = false, length = 50)    
    private String last_name;

    @Basic
    @Column(name = "email", nullable = false, unique = true, length = 90)
    private String email;

    @Basic
    @Column(name = "phone_number", nullable = true, length = 20)
    private String phoneNumber;

    @Basic
    @Column(name = "address", nullable = true, length = 50)
    private String address;

    @Basic
    @Column(name = "city", nullable = false, length = 30)
    private String city;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;

    @OneToOne(optional = false, mappedBy = "user")
    private Cart cart;

}//User