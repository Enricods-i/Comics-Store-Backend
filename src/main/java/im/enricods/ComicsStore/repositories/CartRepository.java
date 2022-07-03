package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.entities.User;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long>{
    
    Cart findByUser(User user);

}//CartRepository
