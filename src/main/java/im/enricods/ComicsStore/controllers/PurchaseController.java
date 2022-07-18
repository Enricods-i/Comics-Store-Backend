package im.enricods.ComicsStore.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Purchase;
import im.enricods.ComicsStore.exceptions.ComicsQuantityUnavaiableException;
import im.enricods.ComicsStore.exceptions.DateWrongRangeException;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;
import im.enricods.ComicsStore.services.PurchaseService;

@RestController
@RequestMapping(path = "/purchases")
public class PurchaseController {
    
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public List<Purchase> getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "purchaseTime") String sortBy){
        return purchaseService.getAllPurchases(pageNumber, pageSize, sortBy);
    }//getAll

    @GetMapping(path = "/user")
    public ResponseEntity<?> getUsersPurchases(@RequestParam(value = "usr") long userId, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "purchaseTime") String sortBy){
        try{
            List<Purchase> result = purchaseService.getAllUsersPurchases(userId);
            return new ResponseEntity<List<Purchase>>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }//getUsersPurchases

    @GetMapping(path = "/inPeriod")
    public ResponseEntity<?> getPurchasesInPeriod(@RequestParam(value = "start") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate, @RequestParam(value = "end") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate){
        try{
            List<Purchase> result = purchaseService.getPurchasesInPeriod(startDate, endDate);
            return new ResponseEntity<List<Purchase>>(result, HttpStatus.OK);
        }
        catch(DateWrongRangeException e){
            return new ResponseEntity<String>("Start date must be previous end date!", HttpStatus.BAD_REQUEST);
        }
    }//getUsersPurchases

    @PostMapping
    public ResponseEntity<?> create(@RequestParam(value = "usr") long userId){
        try{
            Purchase result = purchaseService.addPurchase(userId);
            return new ResponseEntity<Purchase>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        catch(ComicsQuantityUnavaiableException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//create

}//PurchaseController
