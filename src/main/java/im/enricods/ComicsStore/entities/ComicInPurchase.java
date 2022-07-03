package im.enricods.ComicsStore.entities;

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
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "comic_in_purchase")
public class ComicInPurchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "comic_id")
    private Comic comic;

    @ManyToMany
    @JoinTable(
        name = "discount_application",
        joinColumns = @JoinColumn(name = "comic_in_purchase_id"),
        inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    private Set<Discount> discountsApplied;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

}//ComicInPurchase
