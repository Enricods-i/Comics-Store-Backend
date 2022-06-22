package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "category", schema = "public")
public class Category {
    
    @Id
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",nullable = false)
    private Date createdAt;

    @ManyToMany(targetEntity = Collection.class,  mappedBy = "categories")
    private Set<Collection> collections;

}//Category
