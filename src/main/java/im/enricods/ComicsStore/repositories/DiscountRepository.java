package im.enricods.ComicsStore.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountRepository,Long>{
    
    List<Discount> findByExpirationDate(Date expirationDate);

    List<Discount> findByCreationDate(Date creationDate);

}//Discount
