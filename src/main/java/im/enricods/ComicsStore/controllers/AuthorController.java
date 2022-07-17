package im.enricods.ComicsStore.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import im.enricods.ComicsStore.exceptions.AuthorAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.services.AuthorService;

@RestController
@RequestMapping(path = "/authors")
public class AuthorController {
    
    @Autowired
    private AuthorService authorService;

    @GetMapping
    public List<Author> getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        return authorService.showAllAuthors(pageNumber, pageSize, sortBy);
    }//getAll

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid Author author){
        try{
            authorService.addAuthor(author);
        }
        catch(AuthorAlreadyExistsException e){
            return new ResponseEntity<>("Author with name \"" + author.getName() + "\" already exist!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Author \""+ author.getName()  +"\" added succesful!", HttpStatus.OK);
    }//create

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid Author author){
        try{
            authorService.updateAuthor(author);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<>("Author with name \"" + author.getName() + "\" not found!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Author with name \""+ author.getName()  +"\" updated succesful!", HttpStatus.OK);
    }//update

    @DeleteMapping
    public ResponseEntity delete(@RequestParam(value = "authName") String authorName){
        try{
            authorService.deleteAuthor(authorName);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<>("Author with name \"" + authorName + "\" not found!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Author \""+ authorName + "\" deleted succesful!", HttpStatus.OK);
    }//delete

}//AuthorController
