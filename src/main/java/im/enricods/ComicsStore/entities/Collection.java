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
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "collection")
public class Collection {
    
    @EqualsAndHashCode.Include
    @Id
    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "image", length = 20)
    private String image;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "year_of_release")
    private int yearOfRelease;

    @Column(name = "color")
    private boolean color;
   
    @Column(name = "description", length = 1000)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "collection", cascade = CascadeType.MERGE)
    @OrderBy(value = "number asc")
    private List<Comic> comics;

    public void addComic(Comic comic){
        comics.add(comic);
        comic.setCollection(this);
    }//addComic

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "classification",
        joinColumns = @JoinColumn(name = "collection_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    public void bindCategory(Category category){
        categories.add(category);
        category.getCollections().add(this);
    }//bindCategory

    @JsonIgnore
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date creationDate;

}//Collection
