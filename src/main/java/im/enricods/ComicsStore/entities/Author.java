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
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
@Entity @Table(name = "author")
public class Author {

    @NotNull @Min(0)
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull @Size(min = 1, max = 20)
    @Column(length = 20)
    private String name;

    /*
    @Size(max = 20)
    @Column(name = "image", length = 20)
    private String image;
    */

    @Size(max = 1000)
    @Column(length = 1000)
    private String biography;

    @JsonIgnore
    @ManyToMany(mappedBy = "authors")
    private Set<Comic> works;

    public void addWork(Comic comic){
        works.add(comic);
        comic.getAuthors().add(this);
    }

    public void removeWork(Comic comic){
        works.remove(comic);
        comic.getAuthors().remove(this);
    }

    @JsonIgnore
    @Version
    private long version;
    
    @JsonIgnore
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at", nullable = false)
    private Date creationDate;

    @JsonIgnore
    @UpdateTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "modified_at", nullable = false)
    private Date dateOfLastModification;

}//Author
