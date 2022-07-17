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

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.exceptions.CollectionNotFoundException;
import im.enricods.ComicsStore.exceptions.ComicAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.services.ComicService;

@RestController
@RequestMapping(path = "/comics")
public class ComicController {
    
    @Autowired
    private ComicService comicService;

    @GetMapping(path = "/inCollection")
    public ResponseEntity getComicsInCollections(@RequestParam(value = "collection") String collectionName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "number") String sortBy){
        try{
            List<Comic> result = comicService.showComicsInCollection(collectionName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<>("Collection \""+collectionName+"\" not found!", HttpStatus.NOT_FOUND);
        }
    }//getComicsInCollections

    @GetMapping(path = "/inCollection/byAuthor")
    public ResponseEntity getComicsInCollectionsCreatedByAuthor(@RequestParam(value = "collection") String collectionName,@RequestParam(value = "author") String authorName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "number") String sortBy){
        try{
            List<Comic> result = comicService.showComicsInCollectionCreatedByAuthor(collectionName, authorName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<>("Collection \""+collectionName+"\" not found!", HttpStatus.NOT_FOUND);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<>("Author \""+authorName+"\" not found!", HttpStatus.NOT_FOUND);
        }
    }//getComicsInCollectionsCreatedByAuthor

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid Comic comic, @RequestParam(value = "collection") String collectionName){
        try{
            Comic result = comicService.addComic(comic, collectionName);
            return new ResponseEntity<>(result,HttpStatus.OK);
        }
        catch(CollectionNotFoundException e){
            return new ResponseEntity<>("Collection \""+collectionName+"\" not found!", HttpStatus.NOT_FOUND);
        }
        catch(ComicAlreadyExistsException e){
            return new ResponseEntity<>("Comic \""+comic.getNumber()+"\" already exists in collection \""+collectionName+"\" !", HttpStatus.NOT_FOUND);
        }
    }//create

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid Comic comic){
        try{
            comicService.updateComic(comic);
            return new ResponseEntity<>("Comic updated successful!",HttpStatus.OK);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<>("Comic \""+comic.getId()+"\" not found!", HttpStatus.NOT_FOUND);
        }
    }//update

    @PutMapping(path = "addAuthor")
    public ResponseEntity addAuthor(@RequestParam(value = "comic") long comicId, @RequestParam(value = "author") String authorName){
        try{
            comicService.addAuthorToComic(authorName, comicId);
            return new ResponseEntity<>("Author \""+authorName+"\" added successful to comic \""+comicId+"\" !",HttpStatus.OK);
        }
        catch(ComicNotFoundException e){
            return new ResponseEntity<>("Comic \""+comicId+"\" not found!", HttpStatus.NOT_FOUND);
        }
        catch(AuthorNotFoundException e){
            return new ResponseEntity<>("Author \""+authorName+"\" not found!", HttpStatus.NOT_FOUND);
        }
    }//update

}//ComicController
