package im.enricods.ComicsStore.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CartContentId implements Serializable{
    
    private long cart;

    private long comic;

}//CartContentId
