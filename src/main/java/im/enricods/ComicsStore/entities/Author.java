package im.enricods.ComicsStore.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "author")
public class Author {

    @NotNull @Size(min = 1, max = 20)
    @EqualsAndHashCode.Include
    @Id @Column(name = "name", length = 20)
    private String name;

    @Size(max = 500)
    @JsonIgnore
    @Column(name = "bio", length = 500)
    private String biography;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "authors",
        joinColumns = {@JoinColumn(name = "author_id")},
        inverseJoinColumns = {@JoinColumn(name = "comic_id")}
    )
    private Set<Comic> works;
    
    @JsonIgnore
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at", nullable = false)
    private Date creationDate;

}//Author
