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
import im.enricods.ComicsStore.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.exceptions.CollectionNotFoundException;
import im.enricods.ComicsStore.exceptions.ComicAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.services.ComicService;
import im.enricods.ComicsStore.utils.InvalidField;

@RestController
@RequestMapping(path = "/comics")
public class ComicController {
    
    @Autowired
    private ComicService comicService;

    @GetMapping(path = "/inCollection")
    public ResponseEntity<?> getComicsInCollections(@RequestParam(value = "cllctn") String collectionName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "number") String sortBy){
        try{
            List<Comic> result = comicService.showComicsInCollection(collectionName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Comic>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection \""+collectionName+"\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//getComicsInCollections

    @GetMapping(path = "/inCollection/byAuthor")
    public ResponseEntity<?> getComicsInCollectionsCreatedByAuthor(@RequestParam(value = "cllctn") String collectionName,@RequestParam(value = "autr") String authorName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "number") String sortBy){
        try{
            List<Comic> result = comicService.showComicsInCollectionCreatedByAuthor(collectionName, authorName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Comic>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection \""+collectionName+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author \""+authorName+"\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//getComicsInCollectionsCreatedByAuthor

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Comic comic, @RequestParam(value = "cllctn") String collectionName){
        try{
            Comic result = comicService.addComic(comic, collectionName);
            return new ResponseEntity<Comic>(result,HttpStatus.OK);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection \""+collectionName+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(ComicAlreadyExistsException e){
            return new ResponseEntity<String>("Comic \""+comic.getNumber()+"\" already exists in collection \""+collectionName+"\" !", HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Comic comic){
        try{
            comicService.updateComic(comic);
            return new ResponseEntity<String>("Comic updated successful!",HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic \""+comic.getId()+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(ComicAlreadyExistsException e){
            return new ResponseEntity<String>("Comic with ISBN\""+comic.getIsbn()+"\" or with number "+comic.getNumber()+" already exists in its collection!", HttpStatus.BAD_REQUEST);
        }
    }//update

    @PutMapping(path = "addAuthor")
    public ResponseEntity<?> addAuthor(@RequestParam(value = "cmc") long comicId, @RequestParam(value = "autr") String authorName){
        try{
            comicService.addAuthorToComic(authorName, comicId);
            return new ResponseEntity<String>("Author \""+authorName+"\" added successful to comic \""+comicId+"\" !",HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidField> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidField(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidField>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<String>("Comic \""+comicId+"\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author \""+authorName+"\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//update

}//ComicController
