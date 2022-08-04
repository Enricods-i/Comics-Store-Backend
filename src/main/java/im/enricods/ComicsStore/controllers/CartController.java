package im.enricods.ComicsStore.controllers;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.services.CartService;
import im.enricods.ComicsStore.utils.InvalidValue;
import im.enricods.ComicsStore.utils.exceptions.CartNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.ComicAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.ComicNotInCartException;
import im.enricods.ComicsStore.utils.exceptions.ComicsQuantityUnavaiableException;
import im.enricods.ComicsStore.utils.exceptions.UserNotFoundException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping(path = "/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart(@RequestParam(value = "usr") long userId) {
        try{
            Cart result = cartService.getCartByUser(userId);
            return new ResponseEntity<Cart>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User \""+userId+"\" not found",HttpStatus.BAD_REQUEST);
        }
        catch(CartNotFoundException e){
            return new ResponseEntity<String>("Cart for user \""+userId+"\" not found",HttpStatus.BAD_REQUEST);
        }
    }//getCart
    
    @PostMapping
    public ResponseEntity<?> addComic(@RequestParam(value = "usr") long userId, @RequestParam(value = "cmcId") long comicId, @RequestParam(value = "qty") int quantity){
        try{
            cartService.addComicToUsersCart(userId, comicId, quantity);
            return new ResponseEntity<String>("Comic with id \""+ comicId +"\" added succesul!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
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
    }//addComic

    @PutMapping
    public ResponseEntity<?> updateComicQuantity(@RequestParam(value = "usr") long userId, @RequestParam(value = "cmcId") long comicId, @RequestParam(value = "qty") int quantity){
        try{
            cartService.updateComicQuantity(userId, comicId, quantity);
            return new ResponseEntity<String>("Comic quantity updated succesul!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
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
    }//updateComicQuantity

    @DeleteMapping
    public ResponseEntity<?> deleteComic(@RequestParam(value = "usr") long userId, @RequestParam(value = "cmcId") long comicId){
        try{
            cartService.deleteComicFromUsersCart(userId, comicId);
            return new ResponseEntity<String>("Comic deleted succesul!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
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
    }//addComic

}//CartController
