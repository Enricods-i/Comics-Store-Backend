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
import im.enricods.ComicsStore.utils.InvalidValue;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping(path = "/v/all")
    public List<Category> showAll(){
        return categoryService.getAll();
    }//getAll

    @GetMapping(path = "/v")
    public ResponseEntity<?> getByName(@RequestParam(value = "name") String categoryName){
        try {
            List<Category> result = categoryService.getByName(categoryName);
            return new ResponseEntity<List<Category>>(result, HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
    }//getByName

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestParam(value = "name") String categoryName){
        try{
            categoryService.add(categoryName);
            return new ResponseEntity<String>("Category \""+ categoryName +"\" added succesful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//create

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") long categoryId){
        try{
            categoryService.remove(categoryId);
            return new ResponseEntity<String>("Category \""+ categoryId +"\" deleted succesful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//delete

    @PutMapping(path = "/chname/{id}")
    public ResponseEntity<?> updateName(@PathVariable(value = "id") long categoryId, @RequestParam(value = "newName") String newName){
        try {
            categoryService.changeName(categoryId, newName);
            return new ResponseEntity<String>("Category "+categoryId+" renamed successful in \""+newName+"\"", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//updateName

    @PatchMapping(path = "/bind/{id}")
    public ResponseEntity<?> bindCollections(@PathVariable(value = "id") long categoryId, @RequestBody Set<Long> collectionIds){
        try{
            categoryService.bindCollections(categoryId, collectionIds);
            return new ResponseEntity<String>("Collections bound successful!", HttpStatus.OK);
        }
        catch(ConstraintViolationException e){
            return new ResponseEntity<List<InvalidValue>>(InvalidValue.getAllInvalidValues(e), HttpStatus.BAD_REQUEST);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }//bindCollections

}//CategoryController
