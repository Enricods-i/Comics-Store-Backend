package im.enricods.ComicsStore.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.services.ComicService;
import im.enricods.ComicsStore.utils.InvalidValue;
import im.enricods.ComicsStore.utils.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.CollectionNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.ComicAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.ComicNotFoundException;

@RestController
@RequestMapping(path = "/comics")
public class ComicController {
    
    @Autowired
    private ComicService comicService;

    @GetMapping(path = "/inCollection")
    public ResponseEntity<?> getComicsInCollections(@RequestParam(value = "cllctn") long collectionId, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "number") String sortBy){
        try{
            List<Comic> result = comicService.showComicsInCollection(collectionId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Comic>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection \""+collectionId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//getComicsInCollections

    @GetMapping(path = "/inCollection/byAuthor")
    public ResponseEntity<?> getComicsInCollectionsCreatedByAuthor(@RequestParam(value = "cllctn") long collectionId, @RequestParam(value = "autr") long authorId, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "number") String sortBy){
        try{
            List<Comic> result = comicService.showComicsInCollectionCreatedByAuthor(collectionId, authorId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Comic>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection \""+collectionId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author \""+authorId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//getComicsInCollectionsCreatedByAuthor

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Comic comic, @RequestParam(value = "cllctn") long collectionId){
        try{
            Comic result = comicService.addComic(comic, collectionId);
            return new ResponseEntity<Comic>(result,HttpStatus.OK);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection \""+collectionId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(ComicAlreadyExistsException e){
            return new ResponseEntity<String>("Comic \""+comic.getNumber()+"\" already exists in collection \""+collectionId+"\" !", HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Comic comic){
        try{
            comicService.updateComic(comic);
            return new ResponseEntity<String>("Comic updated successful!",HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic \""+comic.getId()+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ComicAlreadyExistsException e){
            return new ResponseEntity<String>("Comic with ISBN\""+comic.getIsbn()+"\" or with number "+comic.getNumber()+" already exists in its collection!", HttpStatus.BAD_REQUEST);
        }
    }//update

    @PutMapping(path = "addAuthor")
    public ResponseEntity<?> addAuthor(@RequestParam(value = "cmc") long comicId, @RequestParam(value = "autr") long authorId){
        try{
            comicService.addAuthorToComic(authorId, comicId);
            return new ResponseEntity<String>("Author \""+authorId+"\" added successful to comic \""+comicId+"\" !",HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic \""+comicId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author \""+authorId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//update

}//ComicController
