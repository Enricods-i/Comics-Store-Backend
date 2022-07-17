package im.enricods.ComicsStore.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.entities.Comic;

@Repository
public interface ComicRepository extends JpaRepository<Comic,Long>{ 

    Page<Comic> findByCollection(Collection collection, Pageable pageable);

    @Query(value = "SELECT c FROM Comic c JOIN c.authors a WHERE c.collection = :coll AND a.name = :authName")
    Page<Comic> findByCollectionAndAuthor(Collection coll, String authName, Pageable pageable);

    boolean existsByIsbn(String isbn);

    Optional<Comic> findByIsbn(String isbn);

}//ComicRepository
