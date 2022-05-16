package im.enricods.ComicsStore.entities;

import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "cart_data", schema = "public")
public class Cart {
    
    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false, updatable = false)
    private BigInteger user;

    @Basic
    @Column(name = "total", nullable = false)
    private float total;

    @Basic
    @Column(name = "size", nullable = false)
    private int size;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;

    @ManyToMany
    @JoinTable(name = "cart_content",
        joinColumns = {@JoinColumn(name = "cart")},
        inverseJoinColumns = {@JoinColumn(name = "comic")}
    )
    private Set<Comic> comicsInCart;

}//Cart
