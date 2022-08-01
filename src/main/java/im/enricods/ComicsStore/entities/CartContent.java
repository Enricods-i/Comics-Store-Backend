package im.enricods.ComicsStore.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "cart_content")
public class CartContent {
    
    @JsonIgnore
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "cart", column = @Column(name = "cart_id")),
        @AttributeOverride(name = "comic", column = @Column(name = "comic_id"))
    })
    private CartContentId id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price_variation", nullable = false)
    private boolean priceVariation;

    @Column(name = "discount_added", nullable = false)
    private boolean discountAdded;

    @Column(name = "discount_terminated", nullable = false)
    private boolean discountTerminated;

    @JsonIgnore
    @MapsId(value = "cart")
    @ManyToOne
    private Cart cart;

    @EqualsAndHashCode.Include
    @MapsId(value = "comic")
    @ManyToOne
    private Comic comic;

}//CartContent
