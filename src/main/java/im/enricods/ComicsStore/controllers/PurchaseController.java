package im.enricods.ComicsStore.controllers;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Purchase;
import im.enricods.ComicsStore.exceptions.CartEmptyException;
import im.enricods.ComicsStore.exceptions.ComicsQuantityUnavaiableException;
import im.enricods.ComicsStore.exceptions.DateWrongRangeException;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;
import im.enricods.ComicsStore.services.PurchaseService;
import im.enricods.ComicsStore.utils.InvalidField;

@RestController
@RequestMapping(path = "/purchases")
public class PurchaseController {
    
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "purchaseTime") String sortBy){
        try{
            List<Purchase> result = purchaseService.getAllPurchases(pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Purchase>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//getAll

    @GetMapping(path = "/user")
    public ResponseEntity<?> getUsersPurchases(@RequestParam(value = "usr") long userId, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "purchaseTime") String sortBy){
        try{
            List<Purchase> result = purchaseService.getAllUsersPurchases(userId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Purchase>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }//getUsersPurchases

    @GetMapping(path = "/inPeriod")
    public ResponseEntity<?> getPurchasesInPeriod(  @RequestParam(value = "start") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
                                                    @RequestParam(value = "end") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
                                                    @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, 
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, 
                                                    @RequestParam(value = "sortBy", defaultValue = "purchaseTime") String sortBy)
    {
        try{
            List<Purchase> result = purchaseService.getPurchasesInPeriod(startDate, endDate, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Purchase>>(result, HttpStatus.OK);
        }
        catch(DateWrongRangeException e){
            return new ResponseEntity<String>("Start date must be previous end date!", HttpStatus.BAD_REQUEST);
        }
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//getUsersPurchases

    @PostMapping
    public ResponseEntity<?> create(@RequestParam(value = "usr") long userId){
        try{
            Purchase result = purchaseService.addPurchase(userId);
            return new ResponseEntity<Purchase>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        catch(CartEmptyException e){
            return new ResponseEntity<String>("Your cart is empty", HttpStatus.BAD_REQUEST);
        }
        catch(ComicsQuantityUnavaiableException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//create

}//PurchaseController
