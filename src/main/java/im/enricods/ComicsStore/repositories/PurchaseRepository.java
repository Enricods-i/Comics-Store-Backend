package im.enricods.ComicsStore.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Purchase;
import im.enricods.ComicsStore.entities.User;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Page<Purchase> findByBuyer(User user, Pageable pageable);

    /*
     * @Query("select p from Purchase p where p.purchaseTime > :startDate and p.purchaseTime < :endDate and p.buyer = :user"
     * )
     * List<Purchase> findByBuyerInPeriod(Date startDate, Date endDate, User user);
     */

    Page<Purchase> findByCreationDateBetween(Date startDate, Date endDate, Pageable pageable);

}// PurchaseRepository
