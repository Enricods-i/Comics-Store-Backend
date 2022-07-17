package im.enricods.ComicsStore.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long>{

    @Query(value = "SELECT d FROM Discount d JOIN d.comicsInPromotion c WHERE c = :comic")
    List<Discount> findByComic(Comic comic);
    
    List<Discount> findByExpirationDateGreaterThan(Date date);

    List<Discount> findByActivationDate(Date activationDate);

}//Discount
