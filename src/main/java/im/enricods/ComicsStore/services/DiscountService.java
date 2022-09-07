package im.enricods.ComicsStore.services;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.repositories.DiscountRepository;

@Service
@Transactional
@Validated
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Transactional(readOnly = true)
    public List<Discount> getAllActiveDiscounts() {

        return discountRepository.findByExpirationDateGreaterThan(new Date());

    }// getAllActiveDiscounts

    @Transactional(readOnly = true)
    public List<Discount> getAllDiscounts(@Min(0) int pageNumber, @Min(0) int pageSize, String sortBy) {

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Discount> pagedResult = discountRepository.findAll(paging);
        return pagedResult.getContent();

    }// getAllDiscounts

    /*
     * Add a Discount if each field is valid and
     * if there is already no discount with the same name and
     * if the activation date of Discount is previous the expiration date and
     * if today's date is previous activation date of Discount
     */
    public Discount add(@Valid Discount discount) {

        // verify that a discount with the same name does not exist
        if (discountRepository.existByName(discount.getName()))
            throw new IllegalArgumentException("Discount named \"" + discount.getName() + "\" already exists.");

        // verify that discount's ActivationDate is previous discount's ExpirationDate
        if (discount.getActivationDate().compareTo(discount.getExpirationDate()) >= 0)
            throw new IllegalArgumentException("Expiration date (" + discount.getExpirationDate()
                    + ") is previous activation date (" + discount.getActivationDate() + ").");

        // verify that today's date is previous to activation date
        if (discount.getActivationDate().compareTo(new Date()) < 0)
            throw new IllegalArgumentException(
                    "Activation date (" + discount.getActivationDate() + ") is previous to today's date.");

        return discountRepository.save(discount);

    }// add

    /*
     * Remove the Discount with id discountId if this Discount exists and
     * if it is not already active
     */
    public void remove(@Min(0) long discountId) {

        // verify that discount exists
        Optional<Discount> disc = discountRepository.findById(discountId);
        if (disc.isEmpty())
            throw new IllegalArgumentException("Discount " + discountId + " not found.");

        Discount target = disc.get();

        // verify that the discount is not yet active
        if (target.getActivationDate().compareTo(new Date()) < 0)
            throw new IllegalArgumentException(
                    "Failed to delete discount " + discountId + ": discount is active or already expired.");

        // remove bidirectional relation with Comic
        Iterator<Comic> it = target.getComicsInPromotion().iterator();
        while (it.hasNext()) {
            it.next().getDiscounts().remove(target);
            it.remove();
        }

        // if discount is not yet active, it is assumed that there are no purchases

        discountRepository.delete(target);

    }// remove

    /*
     * Change percenatge to the Discount with id discountId if this Discount exists
     * and if it is not already active
     */
    public void changePercentage(@Min(0) long discountId, @Min(1) @Max(99) int newPercentage) {

        // verify that discount exists
        Optional<Discount> disc = discountRepository.findById(discountId);
        if (disc.isEmpty())
            throw new IllegalArgumentException("Discount " + discountId + " not found.");

        Discount target = disc.get();

        // verify that the discount is not yet active
        if (target.getActivationDate().compareTo(new Date()) < 0)
            throw new IllegalArgumentException(
                    "Failed to change percentagemof discount " + discountId
                            + ": discount is active or already expired.");

        target.setPercentage(newPercentage);

    }// changePercentage

    /*
     * Change activation date of the Discount with id discountId if this Discount
     * exists
     * and if it is not already active
     * and if the Discount has not been applied to any Comics
     * and if the new activation date is previous to the expiration date of the
     * Discount
     * and if today's date is previous to the new activation date
     */
    public void changeActivationDate(@Min(0) long discountId, Date newActivationDate) {

        // verify that discount exists
        Optional<Discount> disc = discountRepository.findById(discountId);
        if (disc.isEmpty())
            throw new IllegalArgumentException("Discount " + discountId + " not found.");

        Discount target = disc.get();

        // verify that the discount is not yet active
        if (target.getActivationDate().compareTo(new Date()) < 0)
            throw new IllegalArgumentException(
                    "Failed to change activation date of discount " + discountId
                            + ": discount is active or already expired.");

        // verifiy that the discount has not been applied to comics
        if (!target.getComicsInPromotion().isEmpty())
            throw new IllegalArgumentException("Failed to change activation date of discount " + discountId
                    + ": discount was applied to some comics.");

        // verify that today's date is previous to the new activation date
        if (newActivationDate.compareTo(new Date()) < 0)
            throw new IllegalArgumentException(
                    "Failed to change activation date of discount " + discountId
                            + ": new activation date is previous to today's date.");

        // verify that new activation date is previous to the expiration date
        if (target.getExpirationDate().compareTo(newActivationDate) <= 0)
            throw new IllegalArgumentException("Failed to change activation date of discount " + discountId
                    + ": expiration date of the Discount is previous the new activation date.");

        target.setActivationDate(newActivationDate);

    }// changeActivationDate

    /*
     * Change expiration date of the Discount with id discountId if this Discount
     * exists
     * and if it is not already active
     * and if the Discount has not been applied to any Comics
     * and if the activation date of the discount is previous to the new expiration
     */
    public void changeExpirationDate(@Min(0) long discountId, Date newExpirationDate) {

        // verify that discount exists
        Optional<Discount> disc = discountRepository.findById(discountId);
        if (disc.isEmpty())
            throw new IllegalArgumentException("Discount " + discountId + " not found.");

        Discount target = disc.get();

        // verify that the discount is not yet active
        if (target.getActivationDate().compareTo(new Date()) < 0)
            throw new IllegalArgumentException(
                    "Failed to change expiration date of discount " + discountId
                            + ": discount is active or already expired.");

        // verifiy that the discount has not been applied to comics
        if (!target.getComicsInPromotion().isEmpty())
            throw new IllegalArgumentException("Failed to change expiration date of discount " + discountId
                    + ": discount was applied to some comics.");

        if (target.getActivationDate().compareTo(newExpirationDate) >= 0)
            throw new IllegalArgumentException("Failed to change expiration date of discount " + discountId
                    + ": new expiration date is previous the activation date of the Discount.");

        target.setExpirationDate(newExpirationDate);

    }// changeExpirationDate

    public void finish(@Min(0) long discountId){

    }// finish

    public void applyDiscountToComics(@Min(0) long discountId, @NotEmpty Set< @Min(0)Long> comicIds){

    }// applyDiscountToComics

    public void removeDiscountFromComics(@Min(0) long discountId, @NotEmpty Set< @Min(0)Long> comicIds){

    }// removeDiscountFromComics

}// DiscountService
