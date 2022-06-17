package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
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

import lombok.Data;

@Data
@Entity
@Table(name = "personal_data")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Basic
    @Column(name = "last_name", nullable = false, length = 50)    
    private String lastName;

    @Basic
    @Column(name = "email", nullable = false, unique = true, length = 90)
    private String email;

    @Basic
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Basic
    @Column(name = "address", length = 50)
    private String address;

    @Basic
    @Column(name = "city", length = 30)
    private String city;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(/*targetEntity = WishList.class,*/ mappedBy = "owner")
    private Set<WishList> wishLists;

    @OneToMany(mappedBy = "buyer")
    private Set<Purchase> purchases;

}//User