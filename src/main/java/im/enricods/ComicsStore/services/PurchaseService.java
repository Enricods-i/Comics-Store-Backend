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
import im.enricods.ComicsStore.utils.exceptions.DateWrongRangeException;
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
    public List<Purchase> getAll(@Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Purchase> pagedResult = purchaseRepository.findAll(paging);
        return pagedResult.getContent();

    }//getAll


    @Transactional(readOnly = true)
    public List<Purchase> getByUser(@Min(0) long userId, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){
        
        //verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+userId+" not found!");

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return purchaseRepository.findByBuyer(usr.get(), paging).getContent();

    }//getByUser


    @Transactional(readOnly = true)
    public List<Purchase> getInPeriod(Date startDate, Date endDate, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){
        
        //verify that startDate is previous endDate
        if ( startDate.compareTo(endDate) >= 0 )
            throw new DateWrongRangeException();

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return purchaseRepository.findByPurchaseTimeBetween(startDate, endDate, paging).getContent();

    }//getInPeriod


    public Purchase add(@Min(0) long userId){

        //verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+userId+" not found!");

        //create Purchase
        Purchase purchase = new Purchase();
        purchase.setPurchasedComics(new HashSet<ComicInPurchase>());
        purchase.setBuyer(usr.get());
        //persist
        purchase = purchaseRepository.save(purchase);

        //support variables-----------------------
        float total=0, purchasePrice=0;
        int newQuantity=0;
        Comic c = null;
        //----------------------------------------

        Cart usersCart = usr.get().getCart();

        if(usersCart.getContent().isEmpty())
            throw new IllegalArgumentException("Cart of user "+userId+" is empty!");

        //get content of user's cart for get quantity and calculate effective price of each comic and for calculate the purchase's total price
        for(CartContent cc : usersCart.getContent()){

            c = cc.getComic();

            //verify that quantity of the comic is avaiable now
            newQuantity = c.getQuantity() - cc.getQuantity();
            if(newQuantity < 0)
                throw new ComicsQuantityUnavaiableException("Unavaiable quantity for comic "+ c.getNumber()+ " in collection "+ c.getCollection().getName() +"!");
            
            //create new comicInPurchase entry
            ComicInPurchase cip = new ComicInPurchase();

            //bind bidirectional relation with Comic
            cip.setComic(c);
            c.getCopiesSold().add(cip);

            //bind bidirectional relation with Purchase
            cip.setPurchase(purchase);
            purchase.getPurchasedComics().add(cip);

            //calculate effective price of the comic stock
            purchasePrice = c.getCollection().getPrice();
            //appling active discount
            Optional<Discount> disc = discountRepository.findActiveByComic(c);
            if(disc.isPresent()){
                //bind bidirectional relation
                cip.getDiscountsApplied().add(disc.get());
                disc.get().getDiscountedComics().add(cip);
            }
            purchasePrice -= (purchasePrice/100)*disc.get().getPercentage();
            
            //set effective price
            cip.setPurchasePrice(purchasePrice);

            //set quantity of comic in purchase
            cip.setQuantity(cc.getQuantity());
            //update comic's quantity
            c.setQuantity(newQuantity);

            //update total amount
            total += purchasePrice * cc.getQuantity();

            //persist
            comicInPurchaseRepository.save(cip);
        }//for

        //clear user's cart
        cartContentRepository.deleteAll(usersCart.getContent());
        usersCart.getContent().clear();
        usersCart.setSize(0);

        //set purchase's total
        purchase.setTotal(total);
        
        return purchase;

    }//addPurchase

}//PurchaseService
