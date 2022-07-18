package im.enricods.ComicsStore.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Purchase;
import im.enricods.ComicsStore.entities.User;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase,Long> {
    
    List<Purchase> findByBuyer(User user);

    /*
    @Query("select p from Purchase p where p.purchaseTime > :startDate and p.purchaseTime < :endDate and p.buyer = :user")
    List<Purchase> findByBuyerInPeriod(Date startDate, Date endDate, User user);
    */

    List<Purchase> findByPurchaseTimeBetween(Date startDate, Date endDate);

}//PurchaseRepository
