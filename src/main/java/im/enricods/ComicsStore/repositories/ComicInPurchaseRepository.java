package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.ComicInPurchase;

@Repository
public interface ComicInPurchaseRepository extends JpaRepository<ComicInPurchase,Long>{
    
}//ComicInPurchaseRepository
