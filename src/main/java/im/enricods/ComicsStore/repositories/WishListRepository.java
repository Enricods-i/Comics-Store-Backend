package im.enricods.ComicsStore.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.entities.WishList;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    List<WishList> findByOwnerAndNameContaining(User user, String name);

    List<WishList> findByOwner(User user);

    boolean existsByOwnerAndName(User user, String name);

    @Query("SELECT cmc " +
            "FROM WishList wl JOIN wl.content cmc " +
            "WHERE wl = :wishList")
    Page<Comic> getContent(WishList wishList, Pageable pageable);

    @Query("SELECT wl " +
            "FROM WishList wl JOIN wl.content com " +
            "WHERE com = :comic")
    List<WishList> findByComic(Comic comic);

}// WishListRepository
