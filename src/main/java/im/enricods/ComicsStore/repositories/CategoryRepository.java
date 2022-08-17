package im.enricods.ComicsStore.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long>{
    
    boolean existsByName(String name);

    List<Category> findByNameContaining(String name);

}//CategoryRepository
