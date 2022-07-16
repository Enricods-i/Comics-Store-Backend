package im.enricods.ComicsStore.entities;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CartContentId {
    
    private long cart;

    private long comic;

}//CartContentId
