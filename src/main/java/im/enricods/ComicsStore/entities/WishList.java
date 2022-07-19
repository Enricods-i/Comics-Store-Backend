package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "wish_list")
public class WishList {
    
    @NotNull @Min(value = 0)
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    private long id;

    @NotNull @Size(max = 50)
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @JsonIgnore
    @ManyToOne @JoinColumn(name = "user_id")
    private User owner;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "list_content",
        joinColumns = {@JoinColumn(name = "wish_list_id")},
        inverseJoinColumns = {@JoinColumn(name = "comic_id")}
    )
    private Set<Comic> content;

    @Column(name = "notifications")
    private boolean notifications;

    @JsonIgnore
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at", nullable = false)
    private Date creationDate;

}//WishList
