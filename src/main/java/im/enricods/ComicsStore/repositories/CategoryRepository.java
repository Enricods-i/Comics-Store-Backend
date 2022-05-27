package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import im.enricods.ComicsStore.entities.Category;

public interface CategoryRepository extends JpaRepository<Category,String>{
    
}//CategoryRepository
