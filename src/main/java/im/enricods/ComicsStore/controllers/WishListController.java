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

import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.exceptions.UnavaiableWishList;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;
import im.enricods.ComicsStore.exceptions.WishListAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.WishListNotFoundException;
import im.enricods.ComicsStore.services.WishListService;

@RestController
@RequestMapping(path = "/wishLists")
public class WishListController {
    
    @Autowired
    private WishListService wishListService;

    @GetMapping(path = "/searchByName")
    public ResponseEntity<?> getByName(@RequestParam(value = "lName") String listName, @RequestParam("user") long userId){
        try{
            List<WishList> result = wishListService.showUsersListsByName(userId, listName);
            return new ResponseEntity<List<WishList>>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
    }//getByName

    @GetMapping(path = "/user")
    public ResponseEntity<?> getByUser(@RequestParam("user") long userId){
        try{
            List<WishList> result = wishListService.showAllUsersLists(userId);
            return new ResponseEntity<List<WishList>>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
    }//getByUser

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid WishList wishList, @RequestParam("user") long userId){
        try{
            WishList result = wishListService.createUsersList(userId, wishList);
            return new ResponseEntity<WishList>(result, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(WishListAlreadyExistsException e){
            return new ResponseEntity<String>("WishList "+wishList.getName()+" already exists!", HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping
    public ResponseEntity<String> update(@RequestBody @Valid WishList wishList, @RequestParam("user") long userId){
        try{
            wishListService.updateWishList(userId, wishList);
            return new ResponseEntity<String>("WishList "+wishList.getName()+" updated successful!", HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<String>("User "+userId+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(WishListNotFoundException e){
            return new ResponseEntity<String>("WishList "+wishList.getName()+" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(UnavaiableWishList e){
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
    }//update

    @PutMapping(path = "/addComic")
    public ResponseEntity<String> addComicToList(@RequestParam("user") long userId, @RequestParam("comic") long comicId, @RequestParam("list") long wishListId){
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

    @PutMapping(path = "/deleteComic")
    public ResponseEntity<String> deleteComicToList(@RequestParam("user") long userId, @RequestParam("comic") long comicId, @RequestParam("list") long wishListId){
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
