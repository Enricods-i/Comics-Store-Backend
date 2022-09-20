package im.enricods.ComicsStore.controllers;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.services.AuthorService;
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.covers.Cover;
import im.enricods.ComicsStore.utils.covers.Type;

@RestController
@RequestMapping(path = "/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping(path = "/v/all")
    public ResponseEntity<?> showAll(@RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "name") String sortBy) {
        try {
            List<Author> result = authorService.getAll(pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Author>>(result, HttpStatus.OK);
        }
        // if the sortBy parameter has values that are not allowed
        catch (PropertyReferenceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// getAll

    @GetMapping(path = "/v/byName")
    public ResponseEntity<?> showByName(@RequestParam(value = "name") String authorName,
            @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy) {
        try {
            List<Author> result = authorService.getByName(authorName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Author>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        }
        // if the sortBy parameter has values that are not allowed
        catch (PropertyReferenceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// getByName

    @GetMapping(path = "/cover/{id}")
    public ResponseEntity<?> getCover(@PathVariable(value = "id") long authorId) {
        try {
            return new ResponseEntity<byte[]>(Cover.get(Type.AUTHOR.getLabel() + authorId), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<String>("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }// getCover

    @PatchMapping(path = "/chcov/{id}")
    public ResponseEntity<?> chcov(
            @PathVariable(value = "id") long authorId,
            @RequestParam("img") MultipartFile image) {
        try {
            authorService.changeCover(authorId, image);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<String>("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }// chcov

    @PatchMapping(path = "/delcov/{id}")
    public ResponseEntity<?> delcov(@PathVariable(value = "id") long authorId) {
        try {
            authorService.removeCover(authorId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delcov

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody Author author) {
        try {
            Author res = authorService.add(author);
            return new ResponseEntity<Author>(res, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// create

    @PutMapping(path = "/update")
    public ResponseEntity<?> update(@RequestBody Author author) {
        try {
            authorService.modify(author);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// update

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") long authorId) {
        try {
            authorService.remove(authorId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delete

    @PatchMapping(path = "/add_works/{id}")
    public ResponseEntity<?> addWorks(@PathVariable(value = "id") long authorId, @RequestBody Set<Long> comicIds) {
        try {
            authorService.addWorks(authorId, comicIds);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// addWorks

    @PatchMapping(path = "/delete_works/{id}")
    public ResponseEntity<?> deleteWorks(@PathVariable(value = "id") long authorId, @RequestBody Set<Long> comicIds) {
        try {
            authorService.removeWorks(authorId, comicIds);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// addWorks

}// AuthorController
