package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;
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

        //verify that Category with the name specified doesn't already exists
        if(categoryRepository.existsById(categoryName))
            throw new CategoryAlreadyExistsException();
        
        Category c = new Category();
        c.setName(categoryName);

        categoryRepository.save(c);

    }//createCategory


    public void deleteCategory(String categoryName){

        //verify that Category with the name specified already exists
        Optional<Category> resultCategory = categoryRepository.findById(categoryName);
        if(!resultCategory.isPresent())
            throw new CategoryNotFoundException();
        
        Category target = resultCategory.get();

        categoryRepository.delete(target);

        //unbind bidirectional relations
        for(Collection c : target.getCollections())
            c.getCategories().remove(target);
        
    }//deleteCategory

}//CategoryService
