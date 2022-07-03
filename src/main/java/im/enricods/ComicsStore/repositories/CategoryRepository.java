package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,String>{

    //È già presente la ricerca per nome (chiave primaria)
    
}//CategoryRepository
