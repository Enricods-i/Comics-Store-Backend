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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "comic_in_purchase")
public class ComicInPurchase {
    
    @NotNull @Min(0)
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    private long id;

    @JsonIgnore
    @ManyToOne @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne @JoinColumn(name = "comic_id")
    private Comic comic;

    @ManyToMany
    @JoinTable(
        name = "discount_application",
        joinColumns = @JoinColumn(name = "comic_in_purchase_id"),
        inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    private Set<Discount> discountsApplied;

    @NotNull @PositiveOrZero
    @Column(name = "price", nullable = false)
    private float purchasePrice;

    @NotNull @Min(1)
    @Column(name = "quantity", nullable = false)
    private int quantity;

}//ComicInPurchase
