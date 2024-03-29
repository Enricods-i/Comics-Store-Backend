package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "cart_data")
public class Cart {

    @NotNull
    @Min(0)
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @OneToOne(mappedBy = "cart")
    private User user;

    public void bindToUser(User user) {
        this.setUser(user);
        user.setCart(this);
    }// bindToUser

    @OneToMany(mappedBy = "cart")
    private Set<CartContent> content;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private int size;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at", nullable = false)
    private Date dateOfLastModification;

}// Cart
