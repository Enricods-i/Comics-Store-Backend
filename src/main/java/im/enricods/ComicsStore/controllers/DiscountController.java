package im.enricods.ComicsStore.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.exceptions.DateWrongRangeException;
import im.enricods.ComicsStore.exceptions.DiscountNotFoundException;
import im.enricods.ComicsStore.services.DiscountService;
import im.enricods.ComicsStore.utils.InvalidField;

@RestController
@RequestMapping(path = "/discounts")
public class DiscountController {
    
    @Autowired
    private DiscountService discountService;

    @GetMapping
    public List<Discount> getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "activationDate") String sortBy){
        return discountService.getAllDiscounts(pageNumber, pageSize, sortBy);
    }//getAll

    @GetMapping(path = "/actives")
    public List<Discount> getActives(){
        return discountService.getAllActiveDiscounts();
    }//getActives

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Discount discount){
        try{
            Discount result = discountService.addDiscount(discount);
            return new ResponseEntity<Discount>(result,HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(DateWrongRangeException e){
            return new ResponseEntity<String>("Activation date must be previous Expiration date in discount",HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping(path = "/addPromotion")
    public ResponseEntity<String> createPromotion(@RequestParam(value = "dsnt") long discountId, @RequestParam(value = "cmc") long comicId){
        try{
            discountService.addPromotion(discountId, comicId);
            return new ResponseEntity<String>("Comic \""+comicId+"\" is now in discount \""+discountId+"\".",HttpStatus.OK);
        }
        catch(DiscountNotFoundException e){
            return new ResponseEntity<String>("Discount \""+discountId+"\" not found", HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic \""+comicId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//createPromotion

}//DiscountController
