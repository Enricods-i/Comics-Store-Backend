package im.enricods.ComicsStore.entities;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class CartContentId {
    
    private long cart;

    private long comic;

}//CartContentId
