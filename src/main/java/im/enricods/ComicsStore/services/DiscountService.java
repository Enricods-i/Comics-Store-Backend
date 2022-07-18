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

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.exceptions.DateWrongRangeException;
import im.enricods.ComicsStore.exceptions.DiscountNotFoundException;
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.repositories.DiscountRepository;

@Service
@Transactional
public class DiscountService {
    
    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ComicRepository comicRepository;


    @Transactional(readOnly = true)
    public List<Discount> getAllActiveDiscounts(){

        return discountRepository.findByExpirationDateGreaterThan(new Date());

    }//getAllActiveDiscounts


    @Transactional(readOnly = true)
    public List<Discount> getAllDiscounts(int pageNumber, int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Discount> pagedResult = discountRepository.findAll(paging);
        return pagedResult.getContent();

    }//getAllDiscounts


    public Discount addDiscount(Discount discount){

        //verify that discount's ActivationDate is previous discount's ExpirationDate
        if ( discount.getActivationDate().compareTo(discount.getExpirationDate()) >= 0 )
            throw new DateWrongRangeException();

        return discountRepository.save(discount);

    }//addDiscount


    public void addPromotion(long discountId, long comicId){

        //verify that Discount specified by discountId exists
        Optional<Discount> resultDiscount = discountRepository.findById(discountId);
        if(!resultDiscount.isPresent())
            throw new DiscountNotFoundException();

        //verify that Comic specified by comicId exists
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();
        
        //bind bidirectional relation
        resultDiscount.get().addPromotion(resultComic.get());

    }//addPromotion


}//DiscountService
