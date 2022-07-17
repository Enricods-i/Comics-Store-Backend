package im.enricods.ComicsStore.controllers;

import java.util.List;

import javax.validation.Valid;

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
import im.enricods.ComicsStore.exceptions.DiscountNotFoundException;
import im.enricods.ComicsStore.services.DiscountService;

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
    public ResponseEntity create(@RequestBody @Valid Discount discount){
        Discount result = discountService.addDiscount(discount);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }//create

    @PutMapping(path = "/addPromotion")
    public ResponseEntity createPromotion(@RequestParam(value = "discount") long discountId, @RequestParam(value = "comic") long comicId){
        try{
            discountService.addPromotion(discountId, comicId);
            return new ResponseEntity<>("Comic \""+comicId+"\" is now in discount \""+discountId+"\".",HttpStatus.OK);
        }
        catch(DiscountNotFoundException e){
            return new ResponseEntity<>("Discount \""+discountId+"\" not found", HttpStatus.NOT_FOUND);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<>("Comic \""+comicId+"\" not found!", HttpStatus.NOT_FOUND);
        }
    }//createPromotion

}//DiscountController
