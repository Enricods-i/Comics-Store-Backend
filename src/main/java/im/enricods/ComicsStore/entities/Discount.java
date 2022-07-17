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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "discount")
public class Discount {
    
    @EqualsAndHashCode.Include
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

    @JsonIgnore
    @ManyToMany(mappedBy = "discounts")
    @Getter @Setter private Set<Comic> comicsInPromotion;

    public void addPromotion(Comic comic){
        comic.getDiscounts().add(this);
        comicsInPromotion.add(comic);
    }//addPromotion
    
    @JsonIgnore
    @ManyToMany(mappedBy = "discountsApplied")
    @Getter @Setter private Set<ComicInPurchase> discountedComics;

    @JsonIgnore
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    @Getter private Date creationDate;

}//Discount
