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

import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.services.CollectionService;
import im.enricods.ComicsStore.utils.InvalidValue;
import im.enricods.ComicsStore.utils.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.CategoryNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.CollectionAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.CollectionNotFoundException;

@RestController
@RequestMapping(path = "/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @GetMapping(path = "/byName")
    public ResponseEntity<?> getByName(@RequestParam(value = "search") String collectionName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        try{
            List<Collection> result = collectionService.showCollectionsByName(collectionName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
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
    }//getByName

    @GetMapping(path = "/byCategory")
    public ResponseEntity<?>  getByCategory(@RequestParam(value = "ctgr") long categoryId, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        try{
            List<Collection> result = collectionService.showCollectionsByCategory(categoryId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
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
        catch(CategoryNotFoundException e){
            return new ResponseEntity<String>("Category \""+categoryId+"\" not found!",HttpStatus.BAD_REQUEST);
        }
    }//getByCategory

    @GetMapping(path = "/byAuthor")
    public ResponseEntity<?> getByAuthor(@RequestParam(value = "autr") long authorId, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        try{
            List<Collection> result = collectionService.showCollectionsByAuthor(authorId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
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
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author \""+authorId+"\" not found!",HttpStatus.BAD_REQUEST);
        }
    }//getByAuthor

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Collection collection){
        try{
            collectionService.addCollection(collection);
            return new ResponseEntity<String>("Collection \""+collection.getName()+"\" added successful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(CollectionAlreadyExistsException e){
            return new ResponseEntity<String>("Collection \"" + collection.getName() + "\" already exists!",HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Collection collection){
        try{
            collectionService.updateCollection(collection);
            return new ResponseEntity<String>("Collection "+ collection.getName()  +" updated successful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection "+ collection.getName()  +" not found!", HttpStatus.BAD_REQUEST);
        }
    }//update

    @PutMapping(path = "/bindCategory")
    public ResponseEntity<?> bindCategory(@RequestParam(value = "cllctn") long collectionId, @RequestParam(value = "ctgr") long categoryId){
        try{
            collectionService.bindCategoryToCollection(categoryId, collectionId);
            return new ResponseEntity<String>("Collection "+ collectionId  +" binded to "+ categoryId, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(CategoryNotFoundException e){
            return new ResponseEntity<String>("Category \"" + categoryId + "\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection \"" + collectionId + "\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//update
    
}//CollectionService
