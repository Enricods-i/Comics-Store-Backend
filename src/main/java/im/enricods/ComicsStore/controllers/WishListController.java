package im.enricods.ComicsStore.controllers;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
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

import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.services.WishListService;
import im.enricods.ComicsStore.utils.InvalidValue;

@RestController
@RequestMapping(path = "/wishLists")
public class WishListController {
    
    @Autowired
    private WishListService wishListService;

    @GetMapping(path = "/v/byOwnerAndName")
    public ResponseEntity<?> showByOwnerAndName(@RequestParam(value = "usr") long userId, @RequestParam(value = "listName") String listName){
        try{
            List<WishList> result = wishListService.getByOwnerAndName(userId, listName);
            return new ResponseEntity<List<WishList>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//showByOwnerAndName

    @GetMapping(path = "/v/AllByUser")
    public ResponseEntity<?> showAllByUser(@RequestParam(value = "usr") long userId){
        try{
            List<WishList> result = wishListService.getAllByUser(userId);
            return new ResponseEntity<List<WishList>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//showAllByUser

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestParam(value = "usr") long userId, @RequestBody WishList wishList){
        try{
            WishList result = wishListService.add(userId, wishList);
            return new ResponseEntity<WishList>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//create

    @DeleteMapping(path = "/{id}/delete")
    public ResponseEntity<?> delete(@RequestParam(value = "usr") long userId, @PathVariable(value = "id") long wishListId){
        try{
            wishListService.remove(userId, wishListId);
            return new ResponseEntity<String>("Wish list \""+wishListId+"\" deleted successful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//delete

    @PatchMapping(path = "/{id}/chname")
    public ResponseEntity<?> chName(@RequestParam(value = "usr") long userId, @PathVariable(value = "id") long wishListId, @RequestParam("name") String name){
        try{
            wishListService.chName(userId, wishListId, name);
            return new ResponseEntity<String>("WishList name "+wishListId+" updated successful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//chName

    @PatchMapping(path = "/{id}/addComic")
    public ResponseEntity<?> addComic(@RequestParam(value = "usr") long userId, @PathVariable("id") long wishListId, @RequestBody Set<Long> comicIds){
        try{
            wishListService.addComics(userId, wishListId, comicIds);
            return new ResponseEntity<String>("Comics added successful to wish list "+wishListId+" .", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//addComics

    @DeleteMapping(path = "/{id}/deleteComic")
    public ResponseEntity<?> deleteComics(@RequestParam(value = "usr") long userId, @PathVariable("id") long wishListId, @RequestBody Set<Long> comicIds){
        try{
            wishListService.removeComics(userId, wishListId, comicIds);
            return new ResponseEntity<String>("Comics  deleted successful to wish list "+wishListId+" .", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//deleteComics

}//WishListController
