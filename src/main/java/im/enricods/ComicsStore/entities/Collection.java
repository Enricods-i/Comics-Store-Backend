package im.enricods.ComicsStore.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "collection")
public class Collection {
    
    @Id
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "image", length = 100)
    private String image;
   
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "color")
    private boolean color;

    @Column(name = "format_and_binding")
    private String formatAndBinding;

    @OneToMany(mappedBy = "collection")
    private Set<Comic> comics;

    @ManyToMany
    @JoinTable(
        name = "classification",
        joinColumns = @JoinColumn(name = "collection"),
        inverseJoinColumns = @JoinColumn(name = "category")
    )
    private Set<Category> categories;

}//Collection
