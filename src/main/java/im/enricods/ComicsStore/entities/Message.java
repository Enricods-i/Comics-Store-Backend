package im.enricods.ComicsStore.entities;

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
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "message")
public class Message {

    @NotNull @Min(0)
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne @JoinColumn(name = "change_log_id")
    private ChangeLog change;

    @ManyToOne @JoinColumn(name = "user_id")
    private User targetUser;

    @ManyToMany
    @JoinTable(
        name = "involved_list",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "list_id")
    )
    private Set<WishList> involvedLists;

    @NotNull
    @Column(name = "involved_cart", nullable = false)
    private boolean isCartInvolved;

    @JsonInclude
    @Transient
    private String content;

}//Message
