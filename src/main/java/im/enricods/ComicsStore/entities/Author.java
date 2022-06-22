package im.enricods.ComicsStore.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "author")
public class Author {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @ManyToMany
    @JoinTable(name = "authors",
        joinColumns = {@JoinColumn(name = "author")},
        inverseJoinColumns = {@JoinColumn(name = "comic")}
    )
    private Set<Comic> works;
    
}//Author
