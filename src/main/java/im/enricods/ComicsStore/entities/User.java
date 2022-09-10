package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "personal_data")
public class User {

    @NotNull
    @Min(0)
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @NotNull
    @Past
    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date", nullable = false)
    private Date birthDate;

    @NotNull
    @Email
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Size(min = 6, max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Size(min = 2, max = 20)
    @Column(length = 20)
    private String city;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @JsonIdentityReference(alwaysAsId = true)
    @OneToMany(mappedBy = "owner")
    private Set<WishList> wishLists;

    public void addWishList(WishList wishList) {
        this.wishLists.add(wishList);
        wishList.setOwner(this);
    }// addWishList

    public void removeWishList(WishList wishList) {
        this.wishLists.remove(wishList);
        wishList.setOwner(null);
    }// removeWishList

    @JsonIgnore
    @OneToMany(mappedBy = "buyer")
    private Set<Purchase> purchases;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date creationDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date dateOfLastModification;

}// User