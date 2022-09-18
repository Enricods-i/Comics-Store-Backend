package im.enricods.ComicsStore.controllers;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
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

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.services.CategoryService;
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping(path = "/v/all")
    public List<Category> showAll() {
        return categoryService.getAll();
    }// getAll

    @GetMapping(path = "/v/byName")
    public ResponseEntity<?> getByName(@RequestParam(value = "name") String categoryName) {
        try {
            List<Category> result = categoryService.getByName(categoryName);
            return new ResponseEntity<List<Category>>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        }
    }// getByName

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestParam(value = "name") String categoryName) {
        try {
            Category result = categoryService.add(categoryName);
            return new ResponseEntity<Category>(result, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// create

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") long categoryId) {
        try {
            categoryService.remove(categoryId);
            return new ResponseEntity<String>("Category \"" + categoryId + "\" deleted succesfully.", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// delete

    @PutMapping(path = "/chname/{id}")
    public ResponseEntity<?> updateName(@PathVariable(value = "id") long categoryId,
            @RequestParam(value = "name") String newName) {
        try {
            categoryService.changeName(categoryId, newName);
            return new ResponseEntity<String>("Category " + categoryId + " renamed successfully in \"" + newName + "\".",
                    HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// updateName

    @PatchMapping(path = "/bind/{id}")
    public ResponseEntity<?> bindCollections(@PathVariable(value = "id") long categoryId,
            @RequestBody Set<Long> collectionIds) {
        try {
            categoryService.bindCollections(categoryId, collectionIds);
            return new ResponseEntity<String>("Collections "+collectionIds+" bound successfully to category "+categoryId+".", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// bindCollections

    @PatchMapping(path = "/unbind/{id}")
    public ResponseEntity<?> unbindCollections(@PathVariable(value = "id") long categoryId,
            @RequestBody Set<Long> collectionIds) {
        try {
            categoryService.unbindCollections(categoryId, collectionIds);
            return new ResponseEntity<String>("Collections "+collectionIds+" unbound successfully to category "+categoryId+".", HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<Set<Problem>>(Problem.getProblemFromConstraintViolationException(e),
                    HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<Set<Problem>>(e.getProblems(), HttpStatus.BAD_REQUEST);
        }
    }// bindCollections

}// CategoryController
