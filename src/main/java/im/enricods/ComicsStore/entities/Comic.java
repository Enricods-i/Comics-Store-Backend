package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "comic", schema = "public")
public class Comic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "collection")
    private Collection collection;

    @Basic
    @Column(name = "number", nullable = false)
    private int number;

    @Basic
    @Column(name = "price", nullable = false)
    private float price;

    @ManyToMany
    @JoinTable(
        name = "promotion",
        joinColumns = @JoinColumn(name = "comic"),
        inverseJoinColumns = @JoinColumn(name = "discount")
    )
    private Set<Discount> discounts;

    @Basic
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Basic
    @Column(name = "image", length = 100)
    private String image;

    @Basic
    @Column(name = "writers", length = 50)
    private String writers;

    @Basic
    @Column(name = "cartoonists", length = 50)
    private String cartoonists;

    @Basic
    @Column(name = "formatAndBinding", length = 30)
    private String formatAndBinding;

    @Basic
    @Column(name = "pages")
    private int pages;

    @Basic
    @Column(name = "isbn", length = 13, unique = true, nullable = false)
    private String isbn;

    @Basic
    @Column(name = "description", length = 200)
    private String description;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;

    //questa relazione può essere evitata
    @OneToMany(targetEntity = ComicInPurchase.class,  mappedBy = "comic")
    private Set<ComicInPurchase> comicsSold;

    @ManyToMany(targetEntity = WishList.class, mappedBy = "content")
    private Set<WishList> lists;

    //FARE L'ENTITA' PER GLI ELEMENTI NEL CARRELLO

}//Comic
