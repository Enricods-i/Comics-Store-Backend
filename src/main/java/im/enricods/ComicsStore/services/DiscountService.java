package im.enricods.ComicsStore.services;

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
        if( discount.getActivationDate().compareTo(new Date())<0 )
            throw new IllegalArgumentException("Activation date ("+discount.getActivationDate()+") is previous to today's date!");

        return discountRepository.save(discount);

    }//addDiscount


    public Discount modify(@Valid Discount discount){

        //verify that Discount specified exists
        Optional<Discount> disc = discountRepository.findById(discount.getId());
        if(disc.isEmpty())
            throw new IllegalArgumentException("Discount "+discount.getId()+" not found!");

        //verify that expiration date isn't previous today's date
        if(disc.get().getExpirationDate().compareTo(new Date())<0)
            throw new IllegalArgumentException("Discount already expired ("+disc.get().getExpirationDate()+").");

        //verify that expiration date isn't previous today's date
        if(discount.getExpirationDate().compareTo(new Date())<0)
            throw new IllegalArgumentException("New expiration date ("+discount.getExpirationDate()+") is previous to today's date!");

        Discount target = disc.get();

        //discount has been used at least once
        if( (!discount.getActivationDate().equals(target.getActivationDate()) || 
            discount.getPercentage()!=target.getPercentage()) 
            && 
            discountRepository.hasBeenUsed(target) ){

            //create a new discount
            Discount newDiscount = new Discount();
            newDiscount.setName(target.getName());
            newDiscount.setPercentage(target.getPercentage());
            newDiscount.setActivationDate(new Date());
            newDiscount.setExpirationDate(target.getExpirationDate());
            newDiscount.setComicsInPromotion(new HashSet<>());

            //finish existinig discount
            target.setExpirationDate(new Date());

            //bind bidirectional relation
            for(Comic c : target.getComicsInPromotion())
                newDiscount.addPromotion(c);

            //persist
            return discountRepository.save(newDiscount);

        }
        else{

            discount.setVersion(target.getVersion());
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

        //verify that expiration date isn't previous today's date
        if(disc.get().getExpirationDate().compareTo(new Date())<0)
            throw new IllegalArgumentException("Discount already expired ("+disc.get().getExpirationDate()+").");

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


    public void addPromotions(@Min(0) long discountId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds){

        //verify that Discount specified by discountId exists
        Optional<Discount> disc = discountRepository.findById(discountId);
        if(disc.isEmpty())
            throw new IllegalArgumentException("Discount "+discountId+" not found!");

        //verify that expiration date isn't previous today's date
        if(disc.get().getExpirationDate().compareTo(new Date())<0)
            throw new IllegalArgumentException("Discount already expired ("+disc.get().getExpirationDate()+").");

        Set<Comic> comics = new HashSet<>();
        StringBuilder errors = new StringBuilder();

        Optional<Comic> cmc = null;
        for(long id : comicIds){
            cmc = comicRepository.findById(id);

            //verify that Comics specified by comicIds exist
            if(cmc.isEmpty()){
                errors.append("Comic "+id+" not found;");
                continue;
            }

            //verify that Comic doesn't already have an active discount
            if(comicRepository.isDiscounted(cmc.get())){
                errors.append("Comic "+id+" has already a discount active;");
                continue;
            }

            comics.add(cmc.get());
        }//for

        if(errors.length() != 0)
            throw new IllegalArgumentException(errors.append("\nOperation canceled!").toString());

        for(Comic c : comics)
            disc.get().addPromotion(c);

    }//addPromotions


    public Discount finishPromotions(@Min(0) long discountId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds){

        //verify that Discount specified by discountId exists
        Optional<Discount> disc = discountRepository.findById(discountId);
        if(disc.isEmpty())
            throw new IllegalArgumentException("Discount "+discountId+" not found!");
        
        //verify that expiration date isn't previous today's date
        if(disc.get().getExpirationDate().compareTo(new Date())<0)
            throw new IllegalArgumentException("Discount already expired ("+disc.get().getExpirationDate()+").");

        Set<Comic> comics = new HashSet<>();
        StringBuilder errors = new StringBuilder();

        Optional<Comic> cmc = null;
        for(long id : comicIds){
            cmc = comicRepository.findById(id);
    
            //verify that Comics specified by comicIds exist
            if(cmc.isEmpty()){
                errors.append("Comic "+id+" not found;");
                continue;
            }

            comics.add(cmc.get());

        }//for

        if(errors.length() != 0)
            throw new IllegalArgumentException(errors.append("\nOperation canceled!").toString());

        //create a discount without comics(in the HashSet)
        Discount newDiscount = new Discount();
        newDiscount.setName(disc.get().getName());
        newDiscount.setPercentage(disc.get().getPercentage());
        newDiscount.setActivationDate(new Date());
        newDiscount.setExpirationDate(disc.get().getExpirationDate());
        newDiscount.setComicsInPromotion(new HashSet<>());

        //finish discount disc
        disc.get().setExpirationDate(new Date());

        //create correct promotions
        for(Comic c : disc.get().getComicsInPromotion()){
            if(!comics.contains(c))
                newDiscount.addPromotion(c);
        }
        
        //persist
        return discountRepository.save(newDiscount);

    }//finishPromotions

    
    public void removePromotion(@Min(0) long discountId, @Min(0) long comicId){

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
            if( comicRepository.wasBoughtWithDiscount(cmc.get(), disc.get()) )
                throw new IllegalArgumentException("The promotion [comic:"+comicId+",discount:"+discountId+"] has been used and cannot be removed, but it can be finished");
            disc.get().removePromotion(cmc.get());
        }
        else
            throw new IllegalArgumentException("The promotion [comic:"+comicId+",discount:"+discountId+"] does not exist!");

    }//removePromotion


}//DiscountService
