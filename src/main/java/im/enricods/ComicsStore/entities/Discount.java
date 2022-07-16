package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "discount")
public class Discount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Getter private long id;

    @Column(name = "percentage", nullable = false)
    @Getter private int percentage;

    @Temporal(TemporalType.DATE)
    @Column(name = "activation_date", nullable = false)
    @Getter private Date activationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expiration_date", nullable = false)
    @Getter private Date expirationDate;

    @ManyToMany(mappedBy = "discounts")
    @Getter @Setter private Set<Comic> comicsInPromotion;

    public void addPromotion(Comic comic){
        comic.getDiscounts().add(this);
        comicsInPromotion.add(comic);
    }//addPromotion
    
    @ManyToMany(mappedBy = "discountsApplied")
    @Getter @Setter private Set<ComicInPurchase> discountedComics;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    @Getter private Date creationDate;

}//Discount
