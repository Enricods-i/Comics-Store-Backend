package im.enricods.ComicsStore.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "message")
public class Message {
    
    @NotNull @Min(0)
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    private long id;

    @OneToMany @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "read", nullable = false)
    private boolean read;

    @NotNull @Size(max=500)
    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_at", nullable = false)
    private Date creationDate;

}//Message
