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
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "discount")
public class Discount {
    
    @NotNull @Min(0)
    @EqualsAndHashCode.Include  
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(max = 30)
    @Column(length = 30)
    private String name;

    @NotNull @Min(1) @Max(99)
    @Column(nullable = false)
    private int percentage;

    @Temporal(TemporalType.DATE) @Column(name = "activation_date", nullable = false)
    private Date activationDate;

    @Temporal(TemporalType.DATE) @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    @JsonIgnore
    @Version
    private long version;

    @JsonIdentityReference(alwaysAsId = true)
    @ManyToMany(mappedBy = "discounts")
    private Set<Comic> comicsInPromotion;

    public void addPromotion(Comic comic){
        comic.getDiscounts().add(this);
        this.comicsInPromotion.add(comic);
    }//addPromotion

    public void removePromotion(Comic comic){
        comic.getDiscounts().remove(this);
        this.comicsInPromotion.remove(comic);
    }//removePromotion
    
    @JsonIgnore
    @ManyToMany(mappedBy = "discountsApplied")
    private Set<ComicInPurchase> discountedComics;

    public void registerDiscount(ComicInPurchase cip){
        cip.getDiscountsApplied().add(this);
        this.discountedComics.add(cip);
    }//registerDiscount

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at", nullable = false)
    private Date creationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @UpdateTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "modified_at", nullable = false)
    private Date dateOfLastModification;

}//Discount
