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

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.services.WishListService;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.exceptions.BadRequestException;

@RestController
@RequestMapping(path = "/wishLists")
public class WishListController {

    @Autowired
    private WishListService wishListService;

    @GetMapping(path = "/v/byOwnerAndName")
    public ResponseEntity<?> showByOwnerAndName(
            @RequestParam(value = "usr") long userId,
            @RequestParam(value = "name") String listName) {
        try {
            List<WishList> result = wishListService.getByOwnerAndName(userId, listName);
            return new ResponseEntity<List<WishList>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// showByOwnerAndName

    @GetMapping(path = "/v/allByUser")
    public ResponseEntity<?> showAllByUser(@RequestParam(value = "usr") long userId) {
        try {
            List<WishList> result = wishListService.getAllByUser(userId);
            return new ResponseEntity<List<WishList>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// showAllByUser

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(
            @RequestParam(value = "usr") long userId,
            @RequestParam String name) {
        try {
            WishList result = wishListService.add(userId, name);
            return new ResponseEntity<WishList>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// create

    @GetMapping(path = "/content/{id}")
    public ResponseEntity<?> showContent(
            @PathVariable(value = "id") long wishListId,
            @RequestParam(value = "usr") long userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy) {
        try {
            List<Comic> result = wishListService.getContent(userId, wishListId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Comic>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// showContent

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@RequestParam(value = "usr") long userId,
            @PathVariable(value = "id") long wishListId) {
        try {
            wishListService.remove(userId, wishListId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delete

    @PatchMapping(path = "/chname/{id}")
    public ResponseEntity<?> chName(
            @RequestParam(value = "usr") long userId,
            @PathVariable(value = "id") long wishListId,
            @RequestParam("name") String name) {
        try {
            wishListService.changeName(userId, wishListId, name);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// chName

    @PatchMapping(path = "/addcmcs/{id}")
    public ResponseEntity<?> addcmcs(
            @RequestParam(value = "usr") long userId,
            @PathVariable("id") long wishListId,
            @RequestBody Set<Long> comicIds) {
        try {
            wishListService.addComics(userId, wishListId, comicIds);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// addcmcs

    @PatchMapping(path = "/delcmcs/{id}")
    public ResponseEntity<?> delcmcs(
            @RequestParam(value = "usr") long userId,
            @PathVariable("id") long wishListId,
            @RequestBody Set<Long> comicIds) {
        try {
            wishListService.removeComics(userId, wishListId, comicIds);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delcmcs

}// WishListController
