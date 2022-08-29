package im.enricods.ComicsStore.controllers;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.services.ComicService;
import im.enricods.ComicsStore.utils.InvalidValue;

@RestController
@RequestMapping(path = "/comics")
public class ComicController {
    
    @Autowired
    private ComicService comicService;

    @GetMapping(path = "/v/byCollection")
    public ResponseEntity<?> showByCollections(@RequestParam(value = "cllctn") long collectionId, @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "number") String sortBy){
        try{
            List<Comic> result = comicService.getByCollection(collectionId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Comic>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        //sortBy
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//showByCollections

    @GetMapping(path = "/v/byCollectionAndAuthor")
    public ResponseEntity<?> showByCollectionAndAuthor(@RequestParam(value = "cllctn") long collectionId, @RequestParam(value = "auth") long authorId, @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "number") String sortBy){
        try{
            List<Comic> result = comicService.getByCollectionAndAuthor(collectionId, authorId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Comic>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        //sortBy
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//showByCollectionAndAuthor

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody Comic comic, @RequestParam(value = "cllctn") long collectionId){
        try{
            Comic result = comicService.add(comic, collectionId);
            return new ResponseEntity<Comic>(result,HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping(path = "/update")
    public ResponseEntity<?> update(@RequestBody Comic comic){
        try{
            comicService.update(comic);
            return new ResponseEntity<String>("Comic updated successful!",HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//update

    @DeleteMapping(path = "/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable(value = "id") long comicId){
        try{
            comicService.remove(comicId);
            return new ResponseEntity<String>("Comic "+comicId+" deleted successful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//delete

    @PatchMapping(path = "/{id}/addAuthors")
    public ResponseEntity<?> addAuthors(@PathVariable(value = "id") long comicId, @RequestBody Set<Long> authorIds){
        try{
            comicService.addAuthors(comicId, authorIds);
            return new ResponseEntity<String>("Authors added successful to comic \""+comicId+"\" !",HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//addAuthors

    @PatchMapping(path = "/{id}/delAuthors")
    public ResponseEntity<?> delAuthors(@PathVariable(value = "id") long comicId, @RequestBody Set<Long> authorIds){
        try{
            comicService.removeAuthors(comicId, authorIds);
            return new ResponseEntity<String>("Authors deleted successful to comic \""+comicId+"\" !",HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//delAuthors

}//ComicController
