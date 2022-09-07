package im.enricods.ComicsStore.services;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.repositories.CartContentRepository;
import im.enricods.ComicsStore.repositories.ComicInPurchaseRepository;
import im.enricods.ComicsStore.repositories.DiscountRepository;
import im.enricods.ComicsStore.repositories.PurchaseRepository;
import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.utils.exceptions.ComicsQuantityUnavaiableException;
import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.entities.CartContent;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.ComicInPurchase;
import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.entities.Purchase;
import im.enricods.ComicsStore.entities.User;

@Service
@Transactional
@Validated
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartContentRepository cartContentRepository;

    @Autowired
    private ComicInPurchaseRepository comicInPurchaseRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Transactional(readOnly = true)
    public List<Purchase> getAll(@Min(0) int pageNumber, @Min(0) int pageSize, String sortBy) {

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Purchase> pagedResult = purchaseRepository.findAll(paging);
        return pagedResult.getContent();

    }// getAll

    @Transactional(readOnly = true)
    public List<Purchase> getByUser(@Min(0) long userId, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found!");

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return purchaseRepository.findByBuyer(usr.get(), paging).getContent();

    }// getByUser

    @Transactional(readOnly = true)
    public List<Purchase> getInPeriod(Date startDate, Date endDate, @Min(0) int pageNumber, @Min(0) int pageSize,
            String sortBy) {

        // verify that startDate is previous endDate
        if (startDate.compareTo(endDate) >= 0)
            throw new IllegalArgumentException(
                    "End date (" + endDate + ") is previous start date (" + startDate + ")!");

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return purchaseRepository.findByPurchaseTimeBetween(startDate, endDate, paging).getContent();

    }// getInPeriod

    public Purchase add(@Min(0) long userId) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found!");

        // create Purchase
        Purchase purchase = new Purchase();
        purchase.setPurchasedComics(new HashSet<ComicInPurchase>());
        purchase.bindBuyer(usr.get());

        // support variables-----------------------
        float total = 0, 
            purchasePrice = 0;
        int updatedQuantity = 0;
        Comic currentComic = null;
        // ----------------------------------------

        Cart usersCart = usr.get().getCart();

        if (usersCart.getContent().isEmpty())
            throw new IllegalArgumentException("Cart of user " + userId + " is empty!");

        /* get content of user's cart for get quantity and calculate effective price of
        each comic and for calculate the purchase's total price */
        for (CartContent cmcInCart : usersCart.getContent()) {

            currentComic = cmcInCart.getComic();

            // verify that quantity of the comic is avaiable now
            updatedQuantity = currentComic.getQuantity() - cmcInCart.getQuantity();
            if (updatedQuantity < 0)
                throw new ComicsQuantityUnavaiableException("Unavaiable quantity for comic " + currentComic.getNumber()
                        + " in collection " + currentComic.getCollection().getName() + "!");

            // create new comicInPurchase entry
            ComicInPurchase cmcInPurchase = new ComicInPurchase();

            // calculate effective price of the comic stock (appling discount)
            purchasePrice = currentComic.getCollection().getPrice();
            // appling active discount
            Optional<Discount> disc = discountRepository.findActiveByComic(currentComic);
            if (disc.isPresent()) {
                // bind bidirectional relation
                cmcInPurchase.getDiscountsApplied().add(disc.get());
                disc.get().getDiscountedComics().add(cmcInPurchase);
                // update purchasePrice
                purchasePrice -= (purchasePrice / 100) * disc.get().getPercentage();
            }

            // set effective price
            cmcInPurchase.setComicPrice(purchasePrice);

            // set quantity of comic in purchase
            cmcInPurchase.setQuantity(cmcInCart.getQuantity());
            
            // update comic's quantity
            currentComic.setQuantity(updatedQuantity);

            // update total amount
            total += purchasePrice * cmcInCart.getQuantity();

            // bind bidirectional relation with Comic
            cmcInPurchase.setComic(currentComic);
            currentComic.getComicsSold().add(cmcInPurchase);

            // bind bidirectional relation with Purchase
            cmcInPurchase.setPurchase(purchase);
            purchase.getPurchasedComics().add(cmcInPurchase);

            // persist
            comicInPurchaseRepository.save(cmcInPurchase);
        } // for

        // clear user's cart
        cartContentRepository.deleteAll(usersCart.getContent());
        usersCart.getContent().clear();
        usersCart.setSize(0);

        // set purchase's total
        purchase.setTotal(total);

        // persist
        return purchaseRepository.save(purchase);

    }// addPurchase

}// PurchaseService
