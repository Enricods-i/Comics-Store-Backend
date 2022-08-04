package im.enricods.ComicsStore.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Message;
import im.enricods.ComicsStore.entities.User;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long>{

    Optional<Message> findByUser(User user);
    
}//MessageRepository
