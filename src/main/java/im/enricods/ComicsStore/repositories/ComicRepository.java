package im.enricods.ComicsStore.repositories;

import java.util.Optional;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.entities.Comic;

@Repository
public interface ComicRepository extends JpaRepository<Comic,Long>{ 

    @Query(value = "SELECT c FROM Comic c JOIN c.authors a WHERE c.collection = :coll AND a.name = :auth")
    Page<Comic> findByCollectionAndAuthor(Collection coll, Author auth, Pageable pageable);

    boolean existsByIsbn(String isbn);

    Optional<Comic> findByIsbn(String isbn);

}//ComicRepository
