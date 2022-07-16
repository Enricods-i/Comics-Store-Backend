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

        return discountRepository.save(discount);

    }//addDiscount


    public void addPromotion(long discountId, long comicId){

        Optional<Discount> resultDiscount = discountRepository.findById(discountId);
        if(!resultDiscount.isPresent())
            throw new RuntimeException(); //lo sconto non esiste

        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new RuntimeException(); //il fumetto non esiste
        
        resultDiscount.get().addPromotion(resultComic.get());

    }//addPromotion


}//DiscountService
