package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.repositories.CategoryRepository;
import im.enricods.ComicsStore.utils.exceptions.CategoryAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.CategoryNotFoundException;

@Service
@Transactional
@Validated
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> showAllCategories(){

        return categoryRepository.findAll();

    }//showAllCategories


    public void createCategory(@NotNull @Size(min = 1, max = 30) String categoryName){

        //verify that Category with the name specified doesn't already exists
        if(categoryRepository.existsById(categoryName))
            throw new CategoryAlreadyExistsException();
        
        Category c = new Category();
        c.setName(categoryName);

        categoryRepository.save(c);

    }//createCategory


    public void deleteCategory(@NotNull @Size(min = 1, max = 30) String categoryName){

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
