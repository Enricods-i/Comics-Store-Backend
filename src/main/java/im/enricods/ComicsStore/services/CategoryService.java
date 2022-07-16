package im.enricods.ComicsStore.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.exceptions.CategoryAlreadyExistsException;
import im.enricods.ComicsStore.repositories.CategoryRepository;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> showAllCategories(int pageNumber, int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return categoryRepository.findAll(paging).getContent();

    }//showAllCategories


    public Category createCategory(Category category){

        if(categoryRepository.existsById(category.getName()))
            throw new CategoryAlreadyExistsException();
        
        return categoryRepository.save(category);

    }//createCategory

}//CategoryService
