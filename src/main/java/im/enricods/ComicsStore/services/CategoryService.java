package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.exceptions.CategoryAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.CategoryNotFoundException;
import im.enricods.ComicsStore.repositories.CategoryRepository;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> showAllCategories(){

        return categoryRepository.findAll();

    }//showAllCategories


    public void createCategory(String categoryName){

        if(categoryRepository.existsById(categoryName))
            throw new CategoryAlreadyExistsException();
        
        Category c = new Category();
        c.setName(categoryName);

        categoryRepository.save(c);

    }//createCategory


    public void deleteCategory(String categoryName){

        Optional<Category> resultCategory = categoryRepository.findById(categoryName);
        if(!resultCategory.isPresent())
            throw new CategoryNotFoundException();
        
        categoryRepository.delete(resultCategory.get());
        //cascade type remove rimuove la relazione con collection
        
    }//deleteCategory

}//CategoryService
