package im.enricods.ComicsStore.services;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.ProblemCode;
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
    public List<Purchase> getAll(@Min(0) int pageNumber, @Min(0) int pageSize, @NotNull String sortBy) {

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Purchase> pagedResult = purchaseRepository.findAll(paging);
        return pagedResult.getContent();

    }// getAll

    @Transactional(readOnly = true)
    public List<Purchase> getByUser(@Min(0) long userId, @Min(0) int pageNumber, @Min(0) int pageSize,
            @NotNull String sortBy) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.USER_NOT_FOUND, "userId"));

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return purchaseRepository.findByBuyer(usr.get(), paging).getContent();

    }// getByUser

    @Transactional(readOnly = true)
    public List<Purchase> getInPeriod(@NotNull Date startDate, @NotNull Date endDate, @Min(0) int pageNumber,
            @Min(0) int pageSize,
            @NotNull String sortBy) {

        // verify that startDate is previous endDate
        if (startDate.compareTo(endDate) >= 0)
            throw new BadRequestException(new Problem(ProblemCode.DATE_WRONG_RANGE, "startdate","endDate"));

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return purchaseRepository.findByCreationDateBetween(startDate, endDate, paging).getContent();

    }// getInPeriod

    public Purchase add(@Min(0) long userId) {

        /*--------------------------------------------controls-----------------------------------------------------*/
        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.USER_NOT_FOUND, "userId"));

        Cart userCart = usr.get().getCart();

        // verify that the user's cart is not empty
        if (userCart.getContent().isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.CART_IS_EMPTY, "userId"));

        // verify that quantity of the comics is avaiable now
        Problem problemsCQU = new Problem(ProblemCode.COMIC_QUANTITY_UNAVAIABLE);
        for (CartContent cmcInCart : userCart.getContent()) {
            if (cmcInCart.getComic().getQuantity() < cmcInCart.getQuantity())
                problemsCQU.add(Long.toString(cmcInCart.getComic().getId()));
        }

        // check if there has been any problems
        if (!problemsCQU.getInvalidFields().isEmpty())
            throw new BadRequestException(problemsCQU);
        /*-----------------------------------------------------------------------------------------------------------*/

        // create Purchase
        Purchase purchase = new Purchase();
        purchase.setPurchasedComics(new HashSet<ComicInPurchase>());
        purchase.bindBuyer(usr.get());
        purchase = purchaseRepository.save(purchase);

        /*----------------------support variables----------------------*/
        float total = 0,
            purchasePrice = 0;
        Comic currentComic = null;
        /*-------------------------------------------------------------*/

        /*
         * get content of user's cart for get quantity and calculate effective price of
         * each comic and for calculate the purchase's total price
         */
        for (CartContent cmcInCart : userCart.getContent()) {

            currentComic = cmcInCart.getComic();

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
            currentComic.setQuantity(currentComic.getQuantity() - cmcInCart.getQuantity());

            // update total amount
            total += purchasePrice * cmcInCart.getQuantity();

            // bind bidirectional relation with Comic
            cmcInPurchase.setComic(currentComic);
            currentComic.getComicsSold().add(cmcInPurchase);

            // bind bidirectional relation with Purchase
            purchase.addComicInPurchase(cmcInPurchase);

            // persist
            comicInPurchaseRepository.save(cmcInPurchase);
        } // for

        // clear user's cart
        cartContentRepository.deleteAll(userCart.getContent());
        userCart.getContent().clear();
        userCart.setSize(0);

        // set purchase's total
        purchase.setTotal(total);

        // persist
        return purchase;

    }// addPurchase

}// PurchaseService
