package im.enricods.ComicsStore.controllers;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.services.CartService;
import im.enricods.ComicsStore.utils.InvalidValue;
import im.enricods.ComicsStore.utils.authentication.Token;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping(path = "/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;

    @PreAuthorize("hasAuthoriry('user')")
    @GetMapping(path = "/get")
    public ResponseEntity<?> get() {
        try{
            Cart result = cartService.getByUser(Token.getId());
            return new ResponseEntity<Cart>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//getCart
    
    @PreAuthorize("hasAuthoriry('user')")
    @PutMapping(path = "/add")
    public ResponseEntity<?> addComic(@RequestParam(value = "cmc") long comicId, @RequestParam(value = "qty") int quantity){
        try{
            cartService.addComic(Token.getId(), comicId, quantity);
            return new ResponseEntity<String>("Comic with id \""+ comicId +"\" added succesul!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//addComic

    @PreAuthorize("hasAuthoriry('user')")
    @PutMapping(path = "/chqty")
    public ResponseEntity<?> updateComicQuantity(@RequestParam(value = "cmc") long comicId, @RequestParam(value = "qty") int quantity){
        try{
            cartService.updateComicQuantity(Token.getId(), comicId, quantity);
            return new ResponseEntity<String>("Comic quantity updated succesul!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//updateComicQuantity

    @PreAuthorize("hasAuthoriry('user')")
    @DeleteMapping(path = "/{usr}/delete")
    public ResponseEntity<?> deleteComic(@RequestParam(value = "cmc") long comicId){
        try{
            cartService.removeComic(Token.getId(), comicId);
            return new ResponseEntity<String>("Comic deleted succesul!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//deleteComic

}//CartController
