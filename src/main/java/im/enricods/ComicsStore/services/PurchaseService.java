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
import im.enricods.ComicsStore.repositories.PurchaseRepository;
import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.entities.CartContent;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.ComicInPurchase;
import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.entities.Purchase;
import im.enricods.ComicsStore.entities.User;

@Service
@Transactional
public class PurchaseService {
    
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartContentRepository cartContentRepository;


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
            throw new RuntimeException(); //l'utente non esiste

        return purchaseRepository.findByUser(resultUser.get());

    }//getAllUsersPurchases


    @Transactional(readOnly = true)
    public List<Purchase> getUsersPurchasesInPeriod(long userId, Date startDate, Date endDate){
        
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new RuntimeException(); //l'utente non esiste
        
        if ( startDate.compareTo(endDate) >= 0 )
            throw new RuntimeException(); //la data end è precedente alla start

        return purchaseRepository.findByBuyerInPeriod(startDate, endDate, resultUser.get());

    }//getUsersPurchasesInPeriod


    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesByPurchaseTime(Date date){

        return purchaseRepository.findByPurchaseTime(date);

    }//getPurchasesByPurchaseTime


    public Purchase addPurchase(long userId){

        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new RuntimeException(); //l'utente non esiste
        
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
                throw new RuntimeException(); //quantità non disponibile
            cip.setQuantity(cc.getQuantity());

            //aggiorno la quantità del fumetto
            c.setQuantity(newQuantity);

            total += newPrice * cc.getQuantity();
        }//for

        //pulisco il carrello
        cartContentRepository.deleteAll(usersCart.getContent());
        usersCart.getContent().clear();
        usersCart.setSize(0);

        purchase.setTotal(total);
        return purchase;

    }//addPurchase

}//PurchaseService
