package im.enricods.ComicsStore.services;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
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
    public List<Discount> getAllActiveDiscounts(){

        return discountRepository.findByExpirationDateGreaterThan(new Date());

    }//getAllActiveDiscounts


    @Transactional(readOnly = true)
    public List<Discount> getAllDiscounts(@Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Discount> pagedResult = discountRepository.findAll(paging);
        return pagedResult.getContent();

    }//getAllDiscounts


    public Discount add(@Valid Discount discount){

        //verify that discount's ActivationDate is previous discount's ExpirationDate
        if ( discount.getActivationDate().compareTo(discount.getExpirationDate()) >= 0 )
            throw new IllegalArgumentException("Expiration date ("+discount.getExpirationDate()+") is previous activation date ("+discount.getActivationDate()+")!");

        //verify that today's date is previous to activation date
        if( discount.getActivationDate().compareTo(new Date())>0 )
            throw new IllegalArgumentException("Activation date ("+discount.getActivationDate()+") is previous to today's date!");

        discount.setId(0);

        return discountRepository.save(discount);

    }//addDiscount


    public Discount modify(@Valid Discount discount){

        //verify that expiration date isn't previous today's date
        if(discount.getExpirationDate().compareTo(new Date())<0)
            throw new IllegalArgumentException("Expiration date ("+discount.getExpirationDate()+") is previous to today's date!");

        //verify that Discount specified exists
        Optional<Discount> disc = discountRepository.findById(discount.getId());
        if(disc.isEmpty())
            throw new IllegalArgumentException("Discount "+discount.getId()+" not found!");

        Discount target = disc.get();

        //discount has been used at least once
        if( (!discount.getActivationDate().equals(target.getActivationDate()) || 
            discount.getPercentage()!=target.getPercentage()) 
            && 
            discountRepository.hasBeenUsed(target) ){

            //finish existinig discount
            target.setExpirationDate(new Date());
            //create a new discount with specified fields
            discount.setId(0);
            //persist
            return discountRepository.save(discount);

        }
        else{

            discount.setCreationDate(target.getCreationDate());
            //merge
            return discountRepository.save(discount);

        }
    }//modify


    public void finish(@Min(0) long discountId, @NotNull boolean remove){

        //verify that Discount specified exists
        Optional<Discount> disc = discountRepository.findById(discountId);
        if(disc.isEmpty())
            throw new IllegalArgumentException("Discount "+discountId+" not found!");

        Discount target = disc.get();
        
        if(remove){
            if(target.getDiscountedComics().isEmpty()){
                //removing discount
                //unbind bidirectional relation with Comic
                for(Comic cmc : target.getComicsInPromotion())
                    target.removePromotion(cmc);
                discountRepository.delete(target);
            }
            else throw new IllegalArgumentException("Discount "+discountId+" has been used! You can finish it now.");
        }
        else{
            target.setExpirationDate(new Date());
        }
        
    }//finish


    public void addPromotion(@Min(0) long discountId, @Min(0) long comicId){

        //verify that Discount specified by discountId exists
        Optional<Discount> disc = discountRepository.findById(discountId);
        if(disc.isEmpty())
            throw new IllegalArgumentException("Discount "+discountId+" not found!");

        //verify that Comic specified by comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if(cmc.isEmpty())
            throw new IllegalArgumentException("Comic "+comicId+" not found!");

        //verify that Comic doesn't already have an active discount
        if(comicRepository.isDiscounted(cmc.get()))
            throw new IllegalArgumentException("Comic "+comicId+" has already a discount active!");

        disc.get().addPromotion(cmc.get());

    }//addPromotion

    
    public void finishPromotion(@Min(0) long discountId, @Min(0) long comicId, boolean remove){

        //verify that Discount specified by discountId exists
        Optional<Discount> disc = discountRepository.findById(discountId);
        if(disc.isEmpty())
            throw new IllegalArgumentException("Discount "+discountId+" not found!");
        
        //verify that Comic specified by comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if(cmc.isEmpty())
            throw new IllegalArgumentException("Comic "+comicId+" not found!");

        //verify that promotion exists and it has not already been used
        if( disc.get().getComicsInPromotion().contains(cmc.get()) ){
            if(remove){
                if( comicRepository.wasBoughtWithDiscount(cmc.get(), disc.get()) )
                    throw new IllegalArgumentException("This promotion has already been used");
                disc.get().removePromotion(cmc.get());
            }
            else{
                disc.get().setExpirationDate(new Date());
            }
        }//if

    }//finishPromotion


    //first element returned is old discount, the second is the new
    public List<Discount> modifyPromotions(@Valid Discount discount, @NotEmpty Set<@NotNull @Min(0) Long> comicIds){

        //verify that Discount specified exists
        Optional<Discount> disc = discountRepository.findById(discount.getId());
        if(disc.isEmpty())
            throw new IllegalArgumentException("Discount "+discount.getId()+" not found!");

        Set<Comic> forNewDiscount = new HashSet<>();
        Optional<Comic> cmc = null;
        for(Long id : comicIds){
            cmc = comicRepository.findById(id);
            if(cmc.isPresent())
                forNewDiscount.add(cmc.get());
        }//for

        Set<Comic> forOldDiscount = new HashSet<>();
        for(Comic c : disc.get().getComicsInPromotion())
            if(!forNewDiscount.contains(c))
                forOldDiscount.add(c);

        //finish old discount
        disc.get().setExpirationDate(new Date());

        //create a new Discount
        Discount d1 = new Discount();
        d1.setName(disc.get().getName());
        d1.setPercentage(disc.get().getPercentage());
        d1.setActivationDate(new Date());
        d1.setExpirationDate(disc.get().getExpirationDate());
        discountRepository.save(d1);

        //create another new Discount
        discount.setId(0);
        Discount d2 = discountRepository.save(discount);

        d2.getComicsInPromotion().addAll(forNewDiscount);

        return Arrays.asList(d1,d2);

    }//modifyPromotions


}//DiscountService
