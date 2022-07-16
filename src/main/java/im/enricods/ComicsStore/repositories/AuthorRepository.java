package im.enricods.ComicsStore.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long>{

    List<Author> findByName(String name);

    boolean existsByName(String name);
    
}//AuthorRepository
