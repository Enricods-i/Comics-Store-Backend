package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.repositories.CategoryRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;

@Service
@Transactional
@Validated
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CollectionRepository collectionRepository;


    @Transactional(readOnly = true)
    public List<Category> getAll(){

        return categoryRepository.findAll();

    }//showAllCategories


    @Transactional(readOnly = true)
    public List<Category> getByName(@NotNull @Size(min=3, max=30) String categoryName){

        return categoryRepository.findByNameIgnoreCaseContaining(categoryName);

    }//showAllCategories


    public void add(@NotNull @Size(min=1, max=30) String categoryName){

        //verify that Category with the name specified doesn't already exist
        if(categoryRepository.existsByName(categoryName))
            throw new IllegalArgumentException("Category with name \""+categoryName+"\" already exists");
        
        Category c = new Category();
        c.setName(categoryName);

        categoryRepository.save(c);

    }//createCategory


    public void changeName(@NotNull @Min(0) long categoryId, @NotNull @Size(min=3, max=30) String newName){

        //verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if(ctgr.isEmpty())
            throw new IllegalArgumentException("Category "+categoryId+" not found!");
        
        if(categoryRepository.existsByName(newName))
            throw new IllegalArgumentException("Category with name \""+newName+"\" already exists!");
        
        ctgr.get().setName(newName);
        
    }//changeCategoryName


    public void remove(@NotNull @Min(0) long categoryId){

        //verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if(ctgr.isEmpty())
            throw new IllegalArgumentException("Category "+categoryId+" not found!");
        
        Category target = ctgr.get();

        //unbind bidirectional relations
        for(Collection c : target.getCollections())
            c.getCategories().remove(target);
        target.getCollections().clear();

        categoryRepository.delete(target);
        
    }//deleteCategory


    public void bindCollections(@NotNull @Min(0) long categoryId, @NotEmpty Set<@NotNull @Min(0) Long> collectionIds){

        //verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if(ctgr.isEmpty())
            throw new IllegalArgumentException("Category "+categoryId+" not found!");

        Optional<Collection> cllctn = null;
        for(Long id : collectionIds){
            //verify that Collection with current id exists
            cllctn = collectionRepository.findById(id);
            if(cllctn.isPresent())
                ctgr.get().bindCollection(cllctn.get());
        }

    }//bindCollections


    public void unbindCollections(@NotNull @Min(0) long categoryId, @NotEmpty Set<@NotNull @Min(0) Long> collectionIds){

        //verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if(ctgr.isEmpty())
            throw new IllegalArgumentException("Category "+categoryId+" not found!");
        
        Optional<Collection> cllctn = null;
        for(Long id : collectionIds){
            //verify that Collection with current id exists
            cllctn = collectionRepository.findById(id);
             if(cllctn.isPresent())
                 ctgr.get().unbindCollection(cllctn.get());
        }

    }//unbindCollections

}//CategoryService
