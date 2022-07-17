package im.enricods.ComicsStore.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.entities.WishList;

public interface WishListRepository extends JpaRepository<WishList,Long> {

    List<WishList> findByOwnerAndName(User user, String name);

    List<WishList> findByOwner(User user);

    boolean existsByName(String name);

}//WishListRepository
