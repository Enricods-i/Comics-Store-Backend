package im.enricods.ComicsStore.controllers;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping(path = "/searchByName")
    public ResponseEntity<?> getByName(@RequestParam(value = "ctgrName") String categoryName){
        try {
            List<Category> result = categoryService.showCategoriesByName(categoryName);
            return new ResponseEntity<List<Category>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
    }//getByName

    @PostMapping
    public ResponseEntity<?> create(@RequestParam(value = "ctgrName") String categoryName){
        try{
            categoryService.createCategory(categoryName);
            return new ResponseEntity<String>("Category \""+ categoryName +"\" added succesful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(CategoryNotFoundException e){
            return new ResponseEntity<String>("Category \"" + categoryId + "\" not found!", HttpStatus.BAD_REQUEST);
        }
    }//delete

    @PutMapping
    public ResponseEntity<?> updateName(@RequestParam(value = "ctgr") long categoryId, @RequestParam(value = "newName") String newName){
        try {
            categoryService.changeCategoryName(categoryId, newName);
            return new ResponseEntity<String>("Name \""+newName+"\" applied successful to category "+categoryId, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(CategoryNotFoundException e){
            return new ResponseEntity<String>("Category \"" + categoryId + "\" not found!", HttpStatus.BAD_REQUEST);
        }
        catch(CategoryAlreadyExistsException e){
            return new ResponseEntity<String>("Category \"" + newName + "\" already exists!", HttpStatus.BAD_REQUEST);
        }
    }//updateName

}//CategoryController
