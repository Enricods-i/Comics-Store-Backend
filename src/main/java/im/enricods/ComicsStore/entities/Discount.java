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
    
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Getter
    @Column(name = "percentage", nullable = false)
    private int percentage;

    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    @ManyToMany(mappedBy = "discounts")
    private Set<Comic> comicsInPromotion;
    
    /* NON VOGLIO SIA BIDIREZIONALE
    @ManyToMany(mappedBy = "discountsApplied")
    private Set<ComicInPurchase> discountedComics;
    */

    @Getter
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date creationDate;

}//Discount
