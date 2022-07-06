package im.enricods.ComicsStore.repositories;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection,String>{
    
    Page<Collection> findByNameContaining(String name, Pageable pageable);

    Page<Collection> findByPriceLessThanEqual(float price, Pageable pageable);

    Page<Collection> findByFirstReleaseBetween(Date startDate, Date endDate, Pageable pageable);

    @Query("SELECT DISTINCT com.collection FROM Comics com JOIN com.authors auth WHERE auth.name = :author")
    Page<Collection> findByAuthor(String author);

    List<Collection> findByCategories(Set<Category> categories);

}//CollectionRepository
