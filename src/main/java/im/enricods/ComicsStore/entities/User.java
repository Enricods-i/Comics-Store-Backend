package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "personal_data")
public class User {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    
    @Column(name = "last_name", nullable = false, length = 20)    
    private String lastName;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date", nullable = false)
    private Date birthDate;

    
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    
    @Column(name = "city", length = 20)
    private String city;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void addCart(Cart cart){
        this.setCart(cart);
        cart.setUser(this);
    }//addCart
    
    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private Set<WishList> wishLists;

    public void addWishList(WishList wishList){
        wishLists.add(wishList);
        wishList.setOwner(this);
    }//addWishList

    @JsonIgnore
    @OneToMany(mappedBy = "buyer")
    private Set<Purchase> purchases;

    @JsonIgnore
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date creationDate;

    @JsonIgnore
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date dateOfLastModification;

}//User