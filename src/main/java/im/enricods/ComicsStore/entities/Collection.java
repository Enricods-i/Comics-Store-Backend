package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "collection")
public class Collection {
    
    @Id
    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "image", length = 60)
    private String image;
   
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "color")
    private boolean color;

    @Column(name = "format_and_binding", length = 30)
    private String formatAndBinding;

    @JsonIgnore
    @OneToMany(mappedBy = "collection", cascade = CascadeType.MERGE)
    @OrderBy(value = "number asc")
    private List<Comic> comics;

    @ManyToMany
    @JoinTable(
        name = "classification",
        joinColumns = @JoinColumn(name = "collection"),
        inverseJoinColumns = @JoinColumn(name = "category")
    )
    private Set<Category> categories;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date creationDate;

}//Collection
