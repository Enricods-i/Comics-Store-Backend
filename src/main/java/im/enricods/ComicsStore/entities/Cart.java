package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "cart_data")
public class Cart {
    
    @Id
    @Column(name = "user_id")
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    
    @Column(name = "total", nullable = false)
    private float total;

    
    @Column(name = "size", nullable = false)
    private int size;

    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;

    //FARE LA ENTITA' PER GLI ELEMENTI NEL CARRELLO

}//Cart
