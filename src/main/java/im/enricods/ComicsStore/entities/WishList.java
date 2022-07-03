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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "wish_list")
public class WishList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false, length = 70)
    private String name;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "list_content",
        joinColumns = {@JoinColumn(name = "wish_list_id")},
        inverseJoinColumns = {@JoinColumn(name = "comic_id")}
    )
    private Set<Comic> content;

    @Column(name = "notifications")
    private boolean notifications;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date creationDate;

}//WishList
