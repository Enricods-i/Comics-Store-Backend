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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Purchase;
import im.enricods.ComicsStore.services.PurchaseService;
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;

@RestController
@RequestMapping(path = "/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping(path = "/all")
    public ResponseEntity<?> showAll(@RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "creationDate") String sortBy) {
        try {
            List<Purchase> result = purchaseService.getAll(pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Purchase>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        }
        // sortBy
        catch (PropertyReferenceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// showAll

    @GetMapping(path = "/byUser")
    public ResponseEntity<?> showByUser(@RequestParam(value = "usr") long userId,
            @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "creationDate") String sortBy) {
        try {
            List<Purchase> result = purchaseService.getByUser(userId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Purchase>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
        // sortBy
        catch (PropertyReferenceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// showByUser

    @GetMapping(path = "/inPeriod")
    public ResponseEntity<?> showInPeriod(
            @RequestParam(value = "start") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
            @RequestParam(value = "end") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "creationDate") String sortBy) {
        try {
            List<Purchase> result = purchaseService.getInPeriod(startDate, endDate, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Purchase>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
        //sortBy
        catch (PropertyReferenceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// showInPeriod

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestParam(value = "usr") long userId) {
        try {
            Purchase result = purchaseService.add(userId);
            return new ResponseEntity<Purchase>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// create

}// PurchaseController
