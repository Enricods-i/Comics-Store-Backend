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

@Data
@Entity
@Table(name = "personal_data")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    
    @Column(name = "last_name", nullable = false, length = 50)    
    private String lastName;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date", nullable = false)
    private Date birthDate;

    
    @Column(name = "email", nullable = false, unique = true, length = 90)
    private String email;

    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    
    @Column(name = "city", length = 30)
    private String city;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "cart_id")
    private Cart cart;
    
    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "user_id") //user_id è nella tabella wish_list
    private Set<WishList> wishLists;

    public void addWishList(WishList wishList){
        wishLists.add(wishList);
    }//addWishList

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

}//User