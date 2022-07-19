package im.enricods.ComicsStore.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.exceptions.CartNotFoundException;
import im.enricods.ComicsStore.exceptions.ComicAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.exceptions.ComicNotInCartException;
import im.enricods.ComicsStore.exceptions.ComicsQuantityUnavaiableException;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;
import im.enricods.ComicsStore.services.CartService;
import im.enricods.ComicsStore.utils.InvalidField;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping(path = "/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;

    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getCart(@PathVariable long userId) {
        try{
            Cart result = cartService.getCartByUser(userId);
            return new ResponseEntity<Cart>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User \""+userId+"\" not found",HttpStatus.BAD_REQUEST);
        }
        catch(CartNotFoundException e){
            return new ResponseEntity<String>("Cart for user \""+userId+"\" not found",HttpStatus.BAD_REQUEST);
        }
    }//getCart
    
    @PostMapping(path = "/{userId}")
    public ResponseEntity<?> addComic(@PathVariable long userId, @RequestParam(value = "cmcId") long comicId, @RequestParam(value = "qty") int quantity){
        try{
            cartService.addComicToUsersCart(userId, comicId, quantity);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User \""+userId+"\" not found", HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic with id \"" + comicId + "\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ComicAlreadyExistsException e){
            return new ResponseEntity<String>("Comic "+comicId+" is already in Cart", HttpStatus.BAD_REQUEST);
        }
        catch(ComicsQuantityUnavaiableException e){
            return new ResponseEntity<String>("Quantity unavaiable!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("Comic with id \""+ comicId +"\" added succesul!", HttpStatus.OK);
    }//addComic

    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updateComicQuantity(@PathVariable long userId, @RequestParam(value = "cmcId") long comicId, @RequestParam(value = "qty") int quantity){
        try{
            cartService.updateComicQuantity(userId, comicId, quantity);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User \""+userId+"\" not found", HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic with id \"" + comicId + "\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotInCartException e){
            return new ResponseEntity<String>("Comic with id \"" + comicId + "\" not found in your cart!", HttpStatus.BAD_REQUEST);
        }
        catch(ComicsQuantityUnavaiableException e){
            return new ResponseEntity<String>("Quantity unavaiable!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("Comic quantity updated succesul!", HttpStatus.OK);
    }//updateComicQuantity

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<?> deleteComic(@PathVariable long userId, @RequestParam(value = "cmcId") long comicId){
        try{
            cartService.deleteComicFromUsersCart(userId, comicId);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User \""+userId+"\" not found", HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic with id \"" + comicId + "\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotInCartException e){
            return new ResponseEntity<String>("Comic with id \"" + comicId + "\" not found in your cart!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("Comic deleted succesul!", HttpStatus.OK);
    }//addComic

}//CartController
