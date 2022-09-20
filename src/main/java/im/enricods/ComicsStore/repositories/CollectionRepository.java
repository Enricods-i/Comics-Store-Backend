package im.enricods.ComicsStore.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

        boolean existsByName(String name);

        Page<Collection> findByNameContainingIgnoreCase(String name, Pageable pageable);

        List<Collection> findTop9ByOrderByCreationDateDesc();

        @Query("SELECT DISTINCT col " +
                        "FROM Collection col JOIN col.comics com JOIN com.authors auth " +
                        "WHERE auth = :author")
        Page<Collection> findByAuthor(Author author, Pageable pageable);

        @Query("SELECT DISTINCT col " +
                        "FROM Collection col JOIN col.categories cat " +
                        "WHERE cat = :category")
        Page<Collection> findByCategory(Category category, Pageable pageable);

        @Query(         "SELECT DISTINCT col " +
                        "FROM Collection col JOIN col.comics com JOIN com.authors auth JOIN col.categories cat " +
                        "WHERE " +
                        "(LOWER(col.name) LIKE '%'||:name||'%') " +
                        "AND " +
                        "(LOWER(auth.name) LIKE '%'||:author||'%') " +
                        "AND " +
                        "(LOWER(cat.name) LIKE '%'||:category||'%')")
        Page<Collection> advancedSearch(
                        @Param("name") String name,
                        @Param("author") String author,
                        @Param("category") String category,
                        Pageable pageable);

}// CollectionRepository
