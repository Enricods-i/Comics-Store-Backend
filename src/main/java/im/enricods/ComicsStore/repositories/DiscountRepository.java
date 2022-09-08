package im.enricods.ComicsStore.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

	boolean existsByName(String name);

	@Query("SELECT disc " +
			"FROM Discount disc JOIN disc.comicsInPromotion c " +
			"WHERE c = :comic AND disc.activationDate<=CURRENT_DATE AND disc.expirationDate>=CURRENT_DATE")
	Optional<Discount> findActiveByComic(Comic comic);

	List<Discount> findByExpirationDateGreaterThan(Date date);

	@Query("SELECT cmc " +
			"FROM Discount disc JOIN disc.comicsInPromotion cmc " +
			"WHERE :from < disc.expirationDate " +
			"AND :to > disc.activationDate")
	List<Comic> findComicsDiscountedInPeriod(Date from, Date to);

	/*
	 * @Query( "SELECT CASE WHEN COUNT(cip)>0 THEN true ELSE false END "+
	 * "FROM Discount disc JOIN disc.discountedComics cip "+
	 * "WHERE disc = :discount")
	 * boolean hasBeenUsed(Discount discount);
	 */

}// Discount
