package im.enricods.ComicsStore.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "cart_content")
public class CartContent {
    
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "cart", column = @Column(name = "cart_id")),
        @AttributeOverride(name = "comic", column = @Column(name = "comic_id"))
    })
    private CartContentId id;

    @Column(name = "quantity")
    private int quantity;

    @MapsId(value = "cart")
    @ManyToOne
    private Cart cart;

    @MapsId(value = "comic")
    @ManyToOne
    private Comic comic;

}//CartContent
