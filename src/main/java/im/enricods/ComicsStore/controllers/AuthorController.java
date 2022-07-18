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
    public ResponseEntity<String> create(@RequestBody @Valid Author author){
        try{
            authorService.addAuthor(author);
            return new ResponseEntity<String>("Author \""+ author.getName()  +"\" added succesful!", HttpStatus.OK);
        }
        catch(AuthorAlreadyExistsException e){
            return new ResponseEntity<String>("Author with name \"" + author.getName() + "\" already exist!", HttpStatus.BAD_REQUEST);
        }
    }//create

    @PutMapping
    public ResponseEntity<String> update(@RequestBody @Valid Author author){
        try{
            authorService.updateAuthor(author);
            return new ResponseEntity<String>("Author with name \""+ author.getName()  +"\" updated succesful!", HttpStatus.OK);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author with name \"" + author.getName() + "\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//update

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam(value = "authName") String authorName){
        try{
            authorService.deleteAuthor(authorName);
            return new ResponseEntity<String>("Author \""+ authorName + "\" deleted succesful!", HttpStatus.OK);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<String>("Author with name \"" + authorName + "\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//delete

}//AuthorController
