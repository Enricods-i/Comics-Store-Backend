package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author,String>{
    
}//AuthorRepository
