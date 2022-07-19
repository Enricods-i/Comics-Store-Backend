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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "discount")
public class Discount {
    
    @NotNull @Min(value = 0)
    @Getter @EqualsAndHashCode.Include  
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id", nullable = false)
    private long id;

    @NotNull @Min(value = 1) @Max(value = 100)
    @Getter
    @Column(name = "percentage", nullable = false)
    private int percentage;

    @Getter
    @Temporal(TemporalType.DATE) @Column(name = "activation_date", nullable = false)
    private Date activationDate;

    @Getter
    @Temporal(TemporalType.DATE) @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    @Getter @Setter 
    @JsonIgnore
    @ManyToMany(mappedBy = "discounts")
    private Set<Comic> comicsInPromotion;

    public void addPromotion(Comic comic){
        comic.getDiscounts().add(this);
        comicsInPromotion.add(comic);
    }//addPromotion
    
    @Getter @Setter 
    @JsonIgnore
    @ManyToMany(mappedBy = "discountsApplied")
    private Set<ComicInPurchase> discountedComics;

    @Getter
    @JsonIgnore
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at", nullable = false)
    private Date creationDate;

}//Discount
