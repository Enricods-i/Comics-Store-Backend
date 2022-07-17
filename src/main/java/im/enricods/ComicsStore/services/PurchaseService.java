package im.enricods.ComicsStore.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.repositories.CartContentRepository;
import im.enricods.ComicsStore.repositories.ComicInPurchaseRepository;
import im.enricods.ComicsStore.repositories.PurchaseRepository;
import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.entities.CartContent;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.ComicInPurchase;
import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.entities.Purchase;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.exceptions.ComicsQuantityUnavaiableException;
import im.enricods.ComicsStore.exceptions.DateWrongRangeException;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;

@Service
@Transactional
public class PurchaseService {
    
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartContentRepository cartContentRepository;

    @Autowired
    private ComicInPurchaseRepository comicInPurchaseRepository;


    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases(int pageNumber, int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Purchase> pagedResult = purchaseRepository.findAll(paging);
        return pagedResult.getContent();

    }//getAllPurchases


    @Transactional(readOnly = true)
    public List<Purchase> getAllUsersPurchases(long userId){
        
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        return purchaseRepository.findByBuyer(resultUser.get());

    }//getAllUsersPurchases


    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesInPeriod(Date startDate, Date endDate){
        
        if ( startDate.compareTo(endDate) >= 0 )
            throw new DateWrongRangeException();

        return purchaseRepository.findByPurchaseTimeBetween(startDate, endDate);

    }//getUsersPurchasesInPeriod


    public Purchase addPurchase(long userId){

        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        Cart usersCart = resultUser.get().getCart();

        Purchase purchase = new Purchase();
        purchase = purchaseRepository.save(purchase);

        float total = 0;
        float initialPrice, newPrice;
        int newQuantity;

        Comic c = null;
        for(CartContent cc : usersCart.getContent()){
            c = cc.getComic();
            ComicInPurchase cip = new ComicInPurchase();

            //collego il fumetto
            cip.setComic(c);
            c.getCopiesSold().add(cip);

            //collego l'acquisto
            cip.setPurchase(purchase);
            purchase.getPurchasedComics().add(cip);

            initialPrice = c.getCollection().getPrice();
            newPrice = initialPrice;
            //sconti applicati
            for(Discount d : c.getDiscounts()){
                //collego lo sconto
                cip.getDiscountsApplied().add(d);
                d.getDiscountedComics().add(cip);

                newPrice -= (initialPrice/100)*d.getPercentage();
            }//for

            //imposto i parametri
            cip.setPrice(newPrice);

            newQuantity = c.getQuantity() - cc.getQuantity();
            if(newQuantity < 0)
                throw new ComicsQuantityUnavaiableException("Unavaiable quantity for comic "+ c.getNumber()+ " in collection "+ c.getCollection().getName() +" !");
            cip.setQuantity(cc.getQuantity());

            //aggiorno la quantità del fumetto
            c.setQuantity(newQuantity);

            total += newPrice * cc.getQuantity();

            comicInPurchaseRepository.save(cip);
        }//for

        //pulisco il carrello
        cartContentRepository.deleteAll(usersCart.getContent());
        usersCart.getContent().clear();
        usersCart.setSize(0);

        purchase.setTotal(total);
        return purchase;

    }//addPurchase

}//PurchaseService
