package im.enricods.ComicsStore.controllers;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping(path = "/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "activationDate") String sortBy) {
        try {
            List<Discount> result = discountService.getAllDiscounts(pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Discount>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        // sortBy
        catch (PropertyReferenceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// getAll

    @GetMapping(path = "/actives")
    public List<Discount> getActives() {
        return discountService.getAllActiveDiscounts();
    }// getActives

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody Discount discount) {
        try {
            Discount result = discountService.add(discount);
            return new ResponseEntity<Discount>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// create

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") long discountId) {
        try {
            discountService.remove(discountId);
            return new ResponseEntity<String>("Discount " + discountId + " deleted successfully.", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// delete

    @PatchMapping(path = "/chper/{id}")
    public ResponseEntity<?> chper(@PathVariable(value = "id") long discountId,
            @RequestParam(value = "p") int newPercentage) {
        try {
            discountService.changePercentage(discountId, newPercentage);
            return new ResponseEntity<String>("Percentage of Discount " + discountId
                    + " updated successfully to the value " + newPercentage + "%.", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// chper

    @PatchMapping(path = "/chact/{id}")
    public ResponseEntity<?> chact(@PathVariable(value = "id") long discountId,
            @RequestParam(value = "d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date newActivationDate) {
        try {
            discountService.changeActivationDate(discountId, newActivationDate);
            return new ResponseEntity<String>("Activation Date of Discount " + discountId
                    + " updated successfully to " + newActivationDate + ".", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// chact

    @PatchMapping(path = "/chexp/{id}")
    public ResponseEntity<?> chexp(@PathVariable(value = "id") long discountId,
            @RequestParam(value = "d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date newExpirationDate) {
        try {
            discountService.changeExpirationDate(discountId, newExpirationDate);
            return new ResponseEntity<String>("Expiration Date of Discount " + discountId
                    + " updated successfully to " + newExpirationDate + ".", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// chact

    @PatchMapping(path = "/finish/{id}")
    public ResponseEntity<?> finish(@PathVariable(value = "id") long discountId) {
        try {
            discountService.finish(discountId);
            return new ResponseEntity<String>("Discount " + discountId + " finished successfully!", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// finish

    @PatchMapping(path = "/apply/{id}")
    public ResponseEntity<?> apply(@PathVariable(value = "id") long discountId, @RequestBody Set<Long> comicIds) {
        try {
            discountService.applyDiscountToComics(discountId, comicIds);
            return new ResponseEntity<String>("Discount "+discountId+" applied to comics: "+comicIds, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// apply

    @PatchMapping(path = "/rmcmc/{id}")
    public ResponseEntity<?> rmcmc(@PathVariable(value = "id") long discountId, @RequestBody Set<Long> comicIds) {
        try {
            discountService.removeDiscountFromComics(discountId, comicIds);
            return new ResponseEntity<String>("Comics: "+comicIds+" removed from discount: "+discountId, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// rmcmc
    

}// DiscountController
