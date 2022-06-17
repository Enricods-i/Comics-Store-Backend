package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import im.enricods.ComicsStore.entities.Comic;

public interface ComicRepository extends JpaRepository<Comic,Long>{
    
    //List<Comic> 

    Comic findByIsbn(String isbn);

    //metodo per ricercare per scrittore, disegnatore

}//ComicRepository
