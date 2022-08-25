package im.enricods.ComicsStore.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.services.DiscountService;
import im.enricods.ComicsStore.utils.InvalidValue;

@RestController
@RequestMapping(path = "/discounts")
public class DiscountController {
    
    @Autowired
    private DiscountService discountService;

    @PreAuthorize("hasAuthoriry('admin')")
    @GetMapping(path = "/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "activationDate") String sortBy){
        try{
            List<Discount> result = discountService.getAllDiscounts(pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Discount>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        //sortBy
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//getAll

    @PreAuthorize("hasAuthoriry('admin')")
    @GetMapping(path = "/actives")
    public List<Discount> getActives(){
        return discountService.getAllActiveDiscounts();
    }//getActives

    @PreAuthorize("hasAuthoriry('admin')")
    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody Discount discount){
        try{
            Discount result = discountService.add(discount);
            return new ResponseEntity<Discount>(result,HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//create

    @PreAuthorize("hasAuthoriry('admin')")
    @PutMapping(path = "/update")
    public ResponseEntity<?> update(@RequestBody Discount discount){
        try{
            Discount result = discountService.modify(discount);
            return new ResponseEntity<Discount>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//update

    @PreAuthorize("hasAuthoriry('admin')")
    @PatchMapping(path = "/{id}/createPromotion")
    public ResponseEntity<?> createPromotion(@PathVariable(value = "id") long discountId, @RequestParam(value = "cmc") long comicId){
        try{
            discountService.addPromotion(discountId, comicId);
            return new ResponseEntity<String>("Comic \""+comicId+"\" is now in discount \""+discountId+"\".",HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//createPromotion

    @PreAuthorize("hasAuthoriry('admin')")
    @PatchMapping(path = "/{id}/finishPromotion")
    public ResponseEntity<?> finishPromotion(@PathVariable(value = "id") long discountId, @RequestParam(value = "cmc") long comicId, @RequestParam(value = "rm", defaultValue = "false") boolean remove){
        try{
            discountService.finishPromotion(discountId, comicId, remove);
            return new ResponseEntity<String>("Promotion that involves discount "+discountId+" and comic "+comicId+" was finished!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//finishPromotion

    @PreAuthorize("hasAuthoriry('admin')")
    @PatchMapping(path = "/updatePromotions")
    public ResponseEntity<?> updatePromotions(@RequestBody List<Object> payload){
        String errorResponse = "Must send a list of two elements: the first must be a Discount while the second must be a Set of Comic identifiers!";
        try{
            if(payload.size()!=2 || !(payload.get(0) instanceof Discount) || !(payload.get(1) instanceof Set) )
                return new ResponseEntity<String>(errorResponse, HttpStatus.BAD_REQUEST);
            Discount discount = (Discount) payload.get(0);
            @SuppressWarnings("rawtypes")
            Set set = (Set) payload.get(1);
            Set<Long> comicIds = new HashSet<>();
            for(Object o : set){
                if(!(o instanceof Long))
                    return new ResponseEntity<String>(errorResponse, HttpStatus.BAD_REQUEST);
                comicIds.add( (Long)o );
            }
            List<Discount> result = discountService.modifyPromotions(discount, comicIds);
            return new ResponseEntity<List<Discount>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//updatePromotions

}//DiscountController
