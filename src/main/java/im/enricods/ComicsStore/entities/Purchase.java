package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "purchase")
public class Purchase {

    @NotNull
    @Min(0)
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIdentityReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User buyer;

    public void bindBuyer(User usr) {
        this.buyer = usr;
        usr.getPurchases().add(this);
    }

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private float total;

    @OneToMany(mappedBy = "purchase")
    private Set<ComicInPurchase> purchasedComics;

    public void addComicInPurchase(ComicInPurchase comicInPurchase){
        this.purchasedComics.add(comicInPurchase);
        comicInPurchase.setPurchase(this);
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date creationDate;

}// Purchase
