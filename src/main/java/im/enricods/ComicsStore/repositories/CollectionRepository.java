package im.enricods.ComicsStore.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection,Long>{

    boolean existsByName(String name);

    Optional<Collection> findByName(String name);

    
    Page<Collection> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query( "SELECT col "+
            "FROM Collection col JOIN col.comics com JOIN com.authors auth "+
            "WHERE auth = :author")
    Page<Collection> findByAuthor(Author author, Pageable pageable);

    @Query( "SELECT col "+
            "FROM Collection col JOIN col.categories cat "+
            "WHERE cat = :category")
    Page<Collection> findByCategory(Category category, Pageable pageable);

    @Query(value =  "SELECT col " +
                    "FROM Collection col JOIN col.comics com JOIN com.authors auth JOIN col.categories cat " +
                    "WHERE (col.name LIKE :name OR :name IS NULL) AND " +
                    "(auth = :author OR :author IS NULL) AND " +
                    "(cat = :category OR :category IS NULL)" )
    Page<Collection> advancedSearch(String name, Author author, Category category, Pageable pageable);

}//CollectionRepository
