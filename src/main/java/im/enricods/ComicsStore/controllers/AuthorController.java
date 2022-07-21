package im.enricods.ComicsStore.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.services.AuthorService;
import im.enricods.ComicsStore.utils.InvalidValue;
import im.enricods.ComicsStore.utils.exceptions.AuthorAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.AuthorNotFoundException;

@RestController
@RequestMapping(path = "/authors")
public class AuthorController {
    
    @Autowired
    private AuthorService authorService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        try{
            List<Author> result = authorService.showAllAuthors(pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Author>>(result, HttpStatus.OK);
        }
        catch(PropertyReferenceException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//getAll

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Author author){
        try{
            authorService.addAuthor(author);
            return new ResponseEntity<String>("Author \""+ author.getName()  +"\" added succesful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(AuthorAlreadyExistsException e){
            return new ResponseEntity<String>("Author with name \"" + author.getName() + "\" already exist!", HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Author author){
        try{
            authorService.updateAuthor(author);
            return new ResponseEntity<String>("Author with name \""+ author.getName()  +"\" updated succesful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author with name \"" + author.getName() + "\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//update

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam(value = "auth") String authorName){
        try{
            authorService.deleteAuthor(authorName);
            return new ResponseEntity<String>("Author \""+ authorName + "\" deleted succesful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author with name \"" + authorName + "\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//delete

}//AuthorController
