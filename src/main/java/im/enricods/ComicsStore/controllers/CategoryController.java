package im.enricods.ComicsStore.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.services.CategoryService;
import im.enricods.ComicsStore.utils.InvalidValue;
import im.enricods.ComicsStore.utils.exceptions.CategoryAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.CategoryNotFoundException;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<Category> getAll(){
        return categoryService.showAllCategories();
    }//getAll

    @PostMapping
    public ResponseEntity<?> create(@RequestParam(value = "cName") String categoryName){
        try{
            categoryService.createCategory(categoryName);
            return new ResponseEntity<String>("Category \""+ categoryName +"\" added succesful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(CategoryAlreadyExistsException e){
            return new ResponseEntity<String>("Category \"" + categoryName + "\" already exists!", HttpStatus.BAD_REQUEST);
        }
    }//create

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam(value = "ctgr") long categoryId){
        try{
            categoryService.deleteCategory(categoryId);
            return new ResponseEntity<String>("Category \""+ categoryId +"\" deleted succesful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            List<InvalidValue> fieldsViolated = new LinkedList<>();
            for(ConstraintViolation<?> cv : e.getConstraintViolations()){
                fieldsViolated.add(new InvalidValue(cv.getInvalidValue(), cv.getMessage()));
            }
            return new ResponseEntity<List<InvalidValue>>(fieldsViolated, HttpStatus.BAD_REQUEST);
        }
        catch(CategoryNotFoundException e){
            return new ResponseEntity<String>("Category \"" + categoryId + "\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//delete

}//CategoryController
