package im.enricods.ComicsStore.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;

public interface CollectionRepository extends JpaRepository<Collection,String>{
    
    List<Collection> findByCategories(Set<Category> categories);

}//CollectionRepository
