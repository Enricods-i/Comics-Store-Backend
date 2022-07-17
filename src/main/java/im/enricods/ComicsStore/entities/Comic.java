package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "comic")
public class Comic {
    
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    
    @Column(name = "number", nullable = false)
    private int number;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @ManyToMany(mappedBy = "works", cascade = CascadeType.MERGE)
    private Set<Author> authors;

    public void addAuthor(Author author){
        authors.add(author);
        author.getWorks().add(this);
    }//addAuthor
    
    @Column(name = "image", length = 20)
    private String image;

    @Column(name = "pages")
    private int pages;

    @Temporal(TemporalType.DATE)
    @Column(name = "publication_date")
    private Date publicationDate;

    @Column(name = "isbn", length = 13, unique = true, nullable = false)
    private String isbn;
    
    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "promotion",
        joinColumns = @JoinColumn(name = "comic_id"),
        inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    private Set<Discount> discounts;

    @JsonIgnore
    @OneToMany(mappedBy = "comic")
    private Set<ComicInPurchase> copiesSold;

    @JsonIgnore
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @JsonIgnore
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date creationDate;

    @JsonIgnore
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date dateOfLastModification;

}//Comic
