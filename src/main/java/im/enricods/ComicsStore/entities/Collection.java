package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "collection")
public class Collection {
    
    @NotNull @Min(0)
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull @Size(min = 1, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /*
    @Size(min = 1, max = 20)
    @Column(name = "image", length = 20)
    private String image;
    */

    @NotNull @Positive
    @Column(nullable = false)
    private float price;

    @Column(name = "year_of_release")
    private int yearOfRelease;

    @Size(max = 30)
    @Column(name = "format_and_binding", length = 30)
    private String formatAndBinding;

    private boolean color;
   
    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @JsonIgnore
    @Version
    private long version;

    @JsonIgnore
    @OneToMany(mappedBy = "collection")
    @OrderBy(value = "number asc")
    private List<Comic> comics;

    public void addComic(Comic comic){
        comics.add(comic);
        comic.setCollection(this);
    }//addComic

    @ManyToMany
    @JoinTable(
        name = "classification",
        joinColumns = @JoinColumn(name = "collection_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @JsonIgnore
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at", nullable = false)
    private Date creationDate;

    @JsonIgnore
    @UpdateTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "modified_at", nullable = false)
    private Date dateOfLastModification;

}//Collection
