package im.enricods.ComicsStore.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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
import im.enricods.ComicsStore.utils.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.DateWrongRangeException;
import im.enricods.ComicsStore.utils.exceptions.DiscountNotFoundException;

@Service
@Transactional
@Validated
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
    public List<Discount> getAllDiscounts(@Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Discount> pagedResult = discountRepository.findAll(paging);
        return pagedResult.getContent();

    }//getAllDiscounts


    public Discount addDiscount(@Valid Discount discount){

        //verify that discount's ActivationDate is previous discount's ExpirationDate
        if ( discount.getActivationDate().compareTo(discount.getExpirationDate()) >= 0 )
            throw new DateWrongRangeException();

        return discountRepository.save(discount);

    }//addDiscount


    public void addPromotion(@Min(1) long discountId, @Min(1) long comicId){

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
