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
    public List<Collection> getByName(@RequestParam(value = "collection") String collectionName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        return collectionService.showCollectionsByName(collectionName, pageNumber, pageSize, sortBy);
    }//getByName

    @GetMapping(path = "/byCategory")
    public ResponseEntity  getByCategory(@RequestParam(value = "ctgy") String categoryName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        try{
            List<Collection> result = collectionService.showCollectionsByCategory(categoryName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
        }
        catch(CategoryNotFoundException e){
            return new ResponseEntity<>("Category \""+categoryName+"\" not found!",HttpStatus.NOT_FOUND);
        }
    }//getByCategory

    @GetMapping(path = "/byAuthor")
    public ResponseEntity getByAuthor(@RequestParam(value = "authName") String authorName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        try{
            List<Collection> result = collectionService.showCollectionsByAuthor(authorName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<>("Author \""+authorName+"\" not found!",HttpStatus.NOT_FOUND);
        }
    }//getByAuthor

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid Collection collection){
        try{
            collectionService.addCollection(collection);
        }
        catch(CollectionAlreadyExistsException e){
            return new ResponseEntity<>("Collection \"" + collection.getName() + "\" already exists!",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Collection \""+collection.getName()+"\" added successful!", HttpStatus.OK);
    }//create

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid Collection collection){
        try{
            collectionService.updateCollection(collection);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<>("Collection "+ collection.getName()  +" not found!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Collection "+ collection.getName()  +" updated successful!", HttpStatus.OK);
    }//update

    @PutMapping(path = "/bindCategory")
    public ResponseEntity bindCategory(@RequestParam(value = "col") String collectionName, @RequestParam(value = "cat") String categoryName){
        try{
            collectionService.bindCategoryToCollection(categoryName, collectionName);;
        }
        catch(CategoryNotFoundException e){
            return new ResponseEntity<>("Category \"" + categoryName + "\" not found!", HttpStatus.NOT_FOUND);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<>("Collection \"" + collectionName + "\" not found!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Collection "+ collectionName  +" binded to "+ categoryName, HttpStatus.OK);
    }//update
    
}//CollectionService
