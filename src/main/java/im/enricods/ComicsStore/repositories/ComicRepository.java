package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Comic;

@Repository
public interface ComicRepository extends JpaRepository<Comic,Long>{ 

    Comic findByIsbn(String isbn);

}//ComicRepository
