package im.enricods.ComicsStore.controllers;

import java.util.List;
import java.util.Set;

import java.io.IOException;

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

import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.services.CollectionService;
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.covers.Cover;
import im.enricods.ComicsStore.utils.covers.Type;

@RestController
@RequestMapping(path = "/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @GetMapping(path = "/v/byName")
    public ResponseEntity<?> showByName(
            @RequestParam(value = "name") String collectionName,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy) {
        try {
            List<Collection> result = collectionService.getByName(collectionName, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
            // sortBy
        } catch (PropertyReferenceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }// showByName

    @GetMapping(path = "/v/byCategory")
    public ResponseEntity<?> showByCategory(
            @RequestParam(value = "ctgr") long categoryId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy) {
        try {
            List<Collection> result = collectionService.getByCategory(categoryId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
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
    }// showByCategory

    @GetMapping(path = "/v/byAuthor")
    public ResponseEntity<?> showByAuthor(
            @RequestParam(value = "auth") long authorId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy) {
        try {
            List<Collection> result = collectionService.getByAuthor(authorId, pageNumber, pageSize, sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
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
    }// showByAuthor

    @GetMapping(path = "/v/search")
    public ResponseEntity<?> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(value = "ctgr", required = false) String categoryName,
            @RequestParam(value = "auth", required = false) String authorName,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy) {
        try {
            List<Collection> result = collectionService.advancedSearch(name, categoryName, authorName, pageNumber,
                    pageSize,
                    sortBy);
            return new ResponseEntity<List<Collection>>(result, HttpStatus.OK);
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
    }

    @GetMapping(path = "/v/news")
    public ResponseEntity<?> showRecentAdditions() {
        return new ResponseEntity<List<Collection>>(collectionService.getRecentAdditions(), HttpStatus.OK);
    }

    @GetMapping(path = "/cover/{id}")
    public ResponseEntity<?> getCover(@PathVariable(value = "id") long collectionId) {
        try {
            return new ResponseEntity<byte[]>(Cover.get(Type.COLLECTION.getLabel() + collectionId), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<String>("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }// getCover

    @PatchMapping(path = "/chcov/{id}")
    public ResponseEntity<?> chcov(
            @PathVariable(value = "id") long collectionId,
            @RequestParam("img") MultipartFile image) {
        try {
            collectionService.changeCover(collectionId, image);
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
    public ResponseEntity<?> delcov(@PathVariable(value = "id") long collectionId) {
        try {
            collectionService.removeCover(collectionId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delcov

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody Collection collection) {
        try {
            Collection result = collectionService.add(collection);
            return new ResponseEntity<Collection>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// create

    @PutMapping(path = "/update")
    public ResponseEntity<?> update(@RequestBody Collection collection) {
        try {
            collectionService.modify(collection);
            return new ResponseEntity<String>("Collection " + collection.getName() + " updated successful!",
                    HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// update

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") long collectionId) {
        try {
            collectionService.remove(collectionId);
            return new ResponseEntity<String>("Collection \"" + collectionId + "\" deleted successful!", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delete

    @PatchMapping(path = "/bindCategories/{id}")
    public ResponseEntity<?> bindCategories(@PathVariable(value = "id") long collectionId,
            @RequestBody Set<Long> categoryIds) {
        try {
            collectionService.bindCategories(collectionId, categoryIds);
            return new ResponseEntity<String>(
                    "Collection " + collectionId + " bound to categories: " + categoryIds + ".", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// bindCategories

    @PatchMapping(path = "/unbindCategories/{id}")
    public ResponseEntity<?> unbindCategories(@PathVariable(value = "id") long collectionId,
            @RequestBody Set<Long> categoryIds) {
        try {
            collectionService.unbindCategories(collectionId, categoryIds);
            return new ResponseEntity<String>(
                    "Collection " + collectionId + " unbound to categories: " + categoryIds + ".", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// unbindCategories

}// CollectionService
