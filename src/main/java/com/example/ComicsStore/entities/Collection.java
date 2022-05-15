package com.example.ComicsStore.entities;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "collection", schema = "public")
public class Collection {
    
    @Id
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Basic
    @Column(name = "image", length = 100)
    private String image;
   
    @Basic
    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "collection")
    private Set<Comic> comics;

}//Collection
