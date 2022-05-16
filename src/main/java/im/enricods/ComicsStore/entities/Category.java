package im.enricods.ComicsStore.entities;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "category", schema = "public")
public class Category {
    
    @Id
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Basic
    @Column(name = "description",nullable = false, length = 200)
    private String description;

    @ManyToMany(targetEntity = Collection.class,  mappedBy = "categories")
    private Set<Collection> collections;

}//Category
