package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import im.enricods.ComicsStore.entities.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase,Long> {
    
}//PurchaseRepository
