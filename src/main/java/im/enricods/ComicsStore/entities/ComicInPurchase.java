package im.enricods.ComicsStore.entities;

import java.math.BigInteger;
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
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "personal_data", schema = "public")
public class ComicInPurchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "purchase")
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "comic")
    private Comic comic;

    @ManyToMany
    @JoinTable(
        name = "discount_application",
        joinColumns = @JoinColumn(name = "purchased_comic"),
        inverseJoinColumns = @JoinColumn(name = "discount")
    )
    private Set<Discount> discountsApplied;

    @Basic
    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}
