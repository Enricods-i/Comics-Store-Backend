package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    
    @Column(name = "address", length = 50)
    private String address;

    
    @Column(name = "city", length = 30)
    private String city;

    
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany( mappedBy = "owner")
    private Set<WishList> wishLists;

    @OneToMany(mappedBy = "buyer")
    private Set<Purchase> purchases;

}//User