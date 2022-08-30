package im.enricods.ComicsStore.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.entities.CartContent;
import im.enricods.ComicsStore.entities.CartContentId;
import im.enricods.ComicsStore.entities.Comic;

@Repository
public interface CartContentRepository extends JpaRepository<CartContent, CartContentId>{
    
    List<CartContent> findByCart(Cart cart);

    @Query( "SELECT cc "+
            "FROM CartContent cc "+
            "WHERE cc.comic = :comic")
    List<CartContent> findByComic(Comic comic);

}//CartContentRepository
