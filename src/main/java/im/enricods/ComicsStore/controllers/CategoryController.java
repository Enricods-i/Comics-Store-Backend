package im.enricods.ComicsStore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.exceptions.CategoryAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.CategoryNotFoundException;
import im.enricods.ComicsStore.services.CategoryService;

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
    public ResponseEntity<String> create(@RequestParam(value = "name") String categoryName){
        try{
            categoryService.createCategory(categoryName);
        }
        catch(CategoryAlreadyExistsException e){
            return new ResponseEntity<String>("Category \"" + categoryName + "\" already exists!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("Category \""+ categoryName +"\" added succesful!", HttpStatus.OK);
    }//create

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam(value = "name") String categoryName){
        try{
            categoryService.deleteCategory(categoryName);
        }
        catch(CategoryNotFoundException e){
            return new ResponseEntity<String>("Category \"" + categoryName + "\" not found!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("Category \""+ categoryName +"\" deleted succesful!", HttpStatus.OK);
    }//create

}//CategoryController
