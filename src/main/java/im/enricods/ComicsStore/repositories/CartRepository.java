package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import im.enricods.ComicsStore.entities.Cart;

public interface CartRepository extends JpaRepository<Cart,Long>{
    
}//CartRepository
