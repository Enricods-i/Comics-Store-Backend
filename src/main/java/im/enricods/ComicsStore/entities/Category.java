package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "category")
public class Category {
    
    @NotNull @Min(0)
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    private long id;

    @NotNull @Size(min = 1, max = 30)
    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    @JsonIgnore
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at",nullable = false)
    private Date creationDate;

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    private Set<Collection> collections;

}//Category
