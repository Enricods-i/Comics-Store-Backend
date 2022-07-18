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

import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.exceptions.CategoryNotFoundException;
import im.enricods.ComicsStore.exceptions.CollectionAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.CollectionNotFoundException;
import im.enricods.ComicsStore.services.CollectionService;

@RestController
@RequestMapping(path = "/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @GetMapping(path = "/byName")
    public List<Collection> getByName(@RequestParam(value = "search") String collectionName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        return collectionService.showCollectionsByName(collectionName, pageNumber, pageSize, sortBy);
    }//getByName

    @GetMapping(path = "/byCategory")
    public ResponseEntity<?>  getByCategory(@RequestParam(value = "ctgr") String categoryName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        try{
            List<Collection> result = collectionService.showCollectionsByCategory(categoryName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
        }
        catch(CategoryNotFoundException e){
            return new ResponseEntity<String>("Category \""+categoryName+"\" not found!",HttpStatus.BAD_REQUEST);
        }
    }//getByCategory

    @GetMapping(path = "/byAuthor")
    public ResponseEntity<?> getByAuthor(@RequestParam(value = "autr") String authorName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        try{
            List<Collection> result = collectionService.showCollectionsByAuthor(authorName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author \""+authorName+"\" not found!",HttpStatus.BAD_REQUEST);
        }
    }//getByAuthor

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid Collection collection){
        try{
            collectionService.addCollection(collection);
            return new ResponseEntity<String>("Collection \""+collection.getName()+"\" added successful!", HttpStatus.OK);
        }
        catch(CollectionAlreadyExistsException e){
            return new ResponseEntity<String>("Collection \"" + collection.getName() + "\" already exists!",HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping
    public ResponseEntity<String> update(@RequestBody @Valid Collection collection){
        try{
            collectionService.updateCollection(collection);
            return new ResponseEntity<String>("Collection "+ collection.getName()  +" updated successful!", HttpStatus.OK);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection "+ collection.getName()  +" not found!", HttpStatus.BAD_REQUEST);
        }
    }//update

    @PutMapping(path = "/bindCategory")
    public ResponseEntity<String> bindCategory(@RequestParam(value = "cllctn") String collectionName, @RequestParam(value = "ctgr") String categoryName){
        try{
            collectionService.bindCategoryToCollection(categoryName, collectionName);
            return new ResponseEntity<String>("Collection "+ collectionName  +" binded to "+ categoryName, HttpStatus.OK);
        }
        catch(CategoryNotFoundException e){
            return new ResponseEntity<String>("Category \"" + categoryName + "\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<String>("Collection \"" + collectionName + "\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//update
    
}//CollectionService
