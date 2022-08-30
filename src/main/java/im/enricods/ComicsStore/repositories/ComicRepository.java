package im.enricods.ComicsStore.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.Discount;

@Repository
public interface ComicRepository extends JpaRepository<Comic,Long>{

    Optional<Comic> findByCollectionAndNumber(Collection collection, int number);

    Page<Comic> findByCollection(Collection collection, Pageable pageable);

    @Query(value =  "SELECT c "+
                    "FROM Comic c JOIN c.authors a "+
                    "WHERE c.collection = :coll AND a = :auth")
    Page<Comic> findByCollectionAndAuthor(Collection coll, Author auth, Pageable pageable);

    Optional<Comic> findByIsbn(String isbn);

    @Query("SELECT CASE WHEN COUNT(cip)>0 THEN true ELSE false END "+
            "FROM Comic cmc JOIN cmc.comicsSold cip "+
            "WHERE cmc = :comic")
    boolean existsPurchaseContaining(Comic comic);

    @Query( "SELECT CASE WHEN COUNT(disc)>0 THEN true ELSE false END "+
            "FROM Comic cmc JOIN cmc.discounts disc "+
            "WHERE cmc = :comic AND disc.activationDate<=CURRENT_DATE AND disc.expirationDate>CURRENT_DATE")
    boolean isDiscounted(Comic comic);

    @Query( "SELECT CASE WHEN COUNT(cip)>0 THEN true ELSE false END "+
            "FROM Comic cmc JOIN cmc.comicsSold cip JOIN cip.discountsApplied disc "+
            "WHERE cmc = :comic AND disc = :discount")
    boolean wasBoughtWithDiscount(Comic comic, Discount discount);

}//ComicRepository
