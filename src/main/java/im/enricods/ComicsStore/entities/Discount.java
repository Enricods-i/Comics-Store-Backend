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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "discount")
public class Discount {
    
    @NotNull @Min(0)
    @EqualsAndHashCode.Include  
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    private long id;

    @Size(max = 30)
    @Column(name = "name", length = 30)
    private String name;

    @NotNull @Min(1) @Max(100)
    @Column(name = "percentage", nullable = false)
    private int percentage;

    @Temporal(TemporalType.DATE) @Column(name = "activation_date", nullable = false)
    private Date activationDate;

    @Temporal(TemporalType.DATE) @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    @JsonIgnore
    @Version @Column(name = "version", nullable = false)
    private long version;

    @ManyToMany(mappedBy = "discounts")
    private Set<Comic> comicsInPromotion;

    public void addPromotion(Comic comic){
        comic.getDiscounts().add(this);
        comicsInPromotion.add(comic);
    }//addPromotion
    
    @JsonIgnore
    @ManyToMany(mappedBy = "discountsApplied")
    private Set<ComicInPurchase> discountedComics;

    @JsonIgnore
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at", nullable = false)
    private Date creationDate;

    @JsonIgnore
    @UpdateTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "modified_at", nullable = false)
    private Date dateOfLastModification;

}//Discount
