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

import org.hibernate.annotations.CreationTimestamp;

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
    @Column(name = "isbn", length = 13, unique = true)
    private String isbn;

    @Basic
    @Column(name = "description", length = 200)
    private String description;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;

    @OneToMany(targetEntity = ComicInPurchase.class,  mappedBy = "comic")
    private Set<ComicInPurchase> comicsSold;

}//Comic
