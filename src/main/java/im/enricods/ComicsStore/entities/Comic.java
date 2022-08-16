package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "comic")
public class Comic {
    
    @NotNull @Min(0)
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    private long id;

    @JsonIdentityReference
    @ManyToOne @JoinColumn(name = "collection_id")
    private Collection collection;
    
    @NotNull @Min(1)
    @Column(name = "number", nullable = false)
    private int number;

    @NotNull @Min(0)
    @Column(name = "quantity", nullable = false)
    private int quantity;

    /*
    @Size(max = 20)
    @Column(name = "image", length = 20)
    private String image;
    */

    @Min(1)
    @Column(name = "pages")
    private int pages;

    @NotNull @Size(max = 13)
    @Column(name = "isbn", length = 13, unique = true, nullable = false)
    private String isbn;

    @PastOrPresent
    @Temporal(TemporalType.DATE) @Column(name = "publication_date")
    private Date publicationDate;

    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @JsonIgnore
    @Version @Column(name = "version", nullable = false)
    private long version;

    @JsonIdentityReference(alwaysAsId = true)
    @ManyToMany
    @JoinTable(name = "authors",
        joinColumns = {@JoinColumn(name = "comic_id")},
        inverseJoinColumns = {@JoinColumn(name = "author_id")}
    )
    private Set<Author> authors;

    @JsonIgnore
    @ManyToMany
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
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at", nullable = false)
    private Date creationDate;

    @JsonIgnore
    @UpdateTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "modified_at", nullable = false)
    private Date dateOfLastModification;

}//Comic
