package im.enricods.ComicsStore.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Message;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.entities.WishList;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long>{
    
    @Query("SELECT msg FROM Message msg WHERE msg.targetUser = :user AND msg.isCartInvolved = TRUE")
    Set<Message> findCartMessages(User user);

    @Query("SELECT msg FROM Message msg JOIN msg.involvedLists list WHERE msg.targetUser = :user AND list = :wishList")
    Set<Message> findListMessages(User user, WishList wishList);

}//MessageRepository
