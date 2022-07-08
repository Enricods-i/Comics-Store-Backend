package im.enricods.ComicsStore.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection,String>{
    
    Page<Collection> findByNameContaining(String name, Pageable pageable);

    Page<Collection> findByFirstReleaseBetween(Date startDate, Date endDate, Pageable pageable);

    @Query(value = "SELECT DISTINCT c.collection FROM Comics c JOIN c.authors a WHERE a.name = :author")
    Page<Collection> findByAuthor(Author author, Pageable pageable);

    Page<Collection> findByCategory(Category category, Pageable pageable);

}//CollectionRepository
