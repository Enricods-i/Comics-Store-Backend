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

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.services.ComicService;
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.covers.Cover;
import im.enricods.ComicsStore.utils.covers.Type;

@RestController
@RequestMapping(path = "/comics")
public class ComicController {

    @Autowired
    private ComicService comicService;

    @GetMapping(path = "/v/byCollection")
    public ResponseEntity<?> showByCollections(
            @RequestParam(value = "cllctn") long collectionId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "number") String sortBy) {
        try {
            List<Comic> result = comicService.getByCollection(collectionId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Comic>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
        // sortBy
        catch (PropertyReferenceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// showByCollections

    @GetMapping(path = "/v/byCollectionAndAuthor")
    public ResponseEntity<?> showByCollectionAndAuthor(@RequestParam(value = "cllctn") long collectionId,
            @RequestParam(value = "auth") long authorId, @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "number") String sortBy) {
        try {
            List<Comic> result = comicService.getByCollectionAndAuthor(collectionId, authorId, pageNumber, pageSize,
                    sortBy);
            return new ResponseEntity<List<Comic>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
        // sortBy
        catch (PropertyReferenceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// showByCollectionAndAuthor

    @GetMapping(path = "/cover/{id}")
    public ResponseEntity<?> getCover(@PathVariable(value = "id") long comicId) {
        try {
            return new ResponseEntity<byte[]>(Cover.get(Type.COMIC.getLabel() + comicId), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<String>("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }// getCover

    @PatchMapping(path = "/chcov/{id}")
    public ResponseEntity<?> chcov(
            @PathVariable(value = "id") long comicId,
            @RequestParam("img") MultipartFile image) {
        try {
            comicService.changeCover(comicId, image);
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
    public ResponseEntity<?> delcov(@PathVariable(value = "id") long comicId) {
        try {
            comicService.removeCover(comicId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delcov

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody Comic comic, @RequestParam(value = "cllctn") long collectionId) {
        try {
            Comic result = comicService.add(collectionId, comic);
            return new ResponseEntity<Comic>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// create

    @PutMapping(path = "/update")
    public ResponseEntity<?> update(@RequestBody Comic comic) {
        try {
            comicService.update(comic);
            return new ResponseEntity<String>("Comic " + comic.getId() + " updated successfully.", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// update

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") long comicId) {
        try {
            comicService.remove(comicId);
            return new ResponseEntity<String>("Comic " + comicId + " deleted successfully.", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delete

    @PatchMapping(path = "/addAuthors/{id}")
    public ResponseEntity<?> addAuthors(@PathVariable(value = "id") long comicId, @RequestBody Set<Long> authorIds) {
        try {
            comicService.addAuthors(comicId, authorIds);
            return new ResponseEntity<String>("Authors " + authorIds + " added successfully to comic " + comicId + ".",
                    HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// addAuthors

    @PatchMapping(path = "/delAuthors/{id}")
    public ResponseEntity<?> delAuthors(@PathVariable(value = "id") long comicId, @RequestBody Set<Long> authorIds) {
        try {
            comicService.removeAuthors(comicId, authorIds);
            return new ResponseEntity<String>(
                    "Authors " + authorIds + " deleted successfully from comic " + comicId + ".",
                    HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delAuthors

}// ComicController
