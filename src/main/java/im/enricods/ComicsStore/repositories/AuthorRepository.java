package im.enricods.ComicsStore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long>{

    Page<Author> findByNameContaining(String name, org.springframework.data.domain.Pageable paging);
    
}//AuthorRepository
