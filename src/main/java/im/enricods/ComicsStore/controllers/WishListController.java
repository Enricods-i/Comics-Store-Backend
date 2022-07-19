package im.enricods.ComicsStore.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.exceptions.UnavaiableWishList;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;
import im.enricods.ComicsStore.exceptions.WishListAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.WishListNotFoundException;
import im.enricods.ComicsStore.services.WishListService;
import im.enricods.ComicsStore.utils.InvalidField;

@RestController
@RequestMapping(path = "/wishLists")
public class WishListController {
    
    @Autowired
    private WishListService wishListService;

    @GetMapping(path = "/searchByName")
    public ResponseEntity<?> getByName(@RequestParam(value = "lName") String listName, @RequestParam("usr") long userId){
        try{
            List<WishList> result = wishListService.showUsersListsByName(userId, listName);
            return new ResponseEntity<List<WishList>>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
    }//getByName

    @GetMapping(path = "/searchByUser")
    public ResponseEntity<?> getByUser(@RequestParam("usr") long userId){
        try{
            List<WishList> result = wishListService.showAllUsersLists(userId);
            return new ResponseEntity<List<WishList>>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
    }//getByUser

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WishList wishList, @RequestParam("usr") long userId){
        try{
            WishList result = wishListService.createUsersList(userId, wishList);
            return new ResponseEntity<WishList>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(WishListAlreadyExistsException e){
            return new ResponseEntity<String>("WishList "+wishList.getName()+" already exists!", HttpStatus.BAD_REQUEST);
        }
    }//create

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam(value = "list") long wishListId, @RequestParam("usr") long userId){
        try{
            wishListService.deleteUsersList(userId, wishListId);
            return new ResponseEntity<String>("Wish list \""+wishListId+"\" deleted successful!", HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(WishListNotFoundException e){
            return new ResponseEntity<String>("WishList \""+wishListId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(UnavaiableWishList e){
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
    }//delete

    @PutMapping
    public ResponseEntity<?> update(@RequestBody WishList wishList, @RequestParam("usr") long userId){
        try{
            wishListService.updateWishList(userId, wishList);
            return new ResponseEntity<String>("WishList "+wishList.getName()+" updated successful!", HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(TransactionSystemException e){
            if(e.getRootCause() instanceof ConstraintViolationException){
                ConstraintViolationException cve = (ConstraintViolationException)e.getRootCause();
                List<InvalidField> fieldsViolated = new LinkedList<>();
                for(ConstraintViolation<?> cv : cve.getConstraintViolations()){
                    fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
                }
                return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
            }//if
            else
                return new ResponseEntity<String>("SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(WishListNotFoundException e){
            return new ResponseEntity<String>("WishList "+wishList.getName()+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(UnavaiableWishList e){
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
    }//update

    @PutMapping(path = "/addComic")
    public ResponseEntity<String> addComicToList(@RequestParam("usr") long userId, @RequestParam("cmc") long comicId, @RequestParam("list") long wishListId){
        try{
            wishListService.addComicToList(userId, comicId, wishListId);
            return new ResponseEntity<String>("Comic "+comicId+" added successful to wish list "+wishListId+" .", HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic \""+comicId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(WishListNotFoundException e){
            return new ResponseEntity<String>("WishList "+wishListId+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(UnavaiableWishList e){
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
    }//addComicToList

    @DeleteMapping(path = "/deleteComic")
    public ResponseEntity<String> deleteComicToList(@RequestParam("usr") long userId, @RequestParam("cmc") long comicId, @RequestParam("list") long wishListId){
        try{
            wishListService.deleteComicToList(userId, comicId, wishListId);
            return new ResponseEntity<String>("Comic "+comicId+" deleted successful to wish list "+wishListId+" .", HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic \""+comicId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(WishListNotFoundException e){
            return new ResponseEntity<String>("WishList "+wishListId+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(UnavaiableWishList e){
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
    }//addComicToList

}//WishListController
