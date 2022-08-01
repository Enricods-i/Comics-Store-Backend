package im.enricods.ComicsStore.repositories;

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
    
    Page<Collection> findByNameContaining(String name, Pageable pageable);

    // @Query(value = "SELECT DISTINCT c.collection FROM Comic c JOIN c.authors a WHERE a = :author ORDER BY :sortBy ASC")
    @Query(value = "SELECT col FROM Collection col JOIN col.comics com JOIN com.authors auth WHERE auth = :author")
    Page<Collection> findByAuthor(Author author, Pageable pageable);

    @Query(value = "SELECT col FROM Collection col JOIN col.categories cat WHERE cat = :category")
    Page<Collection> findByCategory(Category category, Pageable pageable);

    @Query(value = "SELECT COUNT(cip) FROM Collection col JOIN col.comics com JOIN com.copiesSold cip WHERE col = :collection")
    int countPurchasesInCollection(Collection collection);

}//CollectionRepository
