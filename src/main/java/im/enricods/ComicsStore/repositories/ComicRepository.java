package im.enricods.ComicsStore.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Comic;

@Repository
public interface ComicRepository extends JpaRepository<Comic,Long>{
    
    List<Comic> findByPublicationDateBetween(Date startDate, Date endDate); 

    Comic findByIsbn(String isbn);

}//ComicRepository
