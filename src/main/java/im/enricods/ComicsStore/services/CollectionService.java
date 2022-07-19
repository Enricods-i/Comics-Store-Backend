package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.exceptions.CategoryNotFoundException;
import im.enricods.ComicsStore.exceptions.CollectionAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.CollectionNotFoundException;
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.repositories.CategoryRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;

@Service
@Transactional
@Validated
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthorRepository authorRepository;
    
    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByName(@Size(min = 1, max = 50) String name, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByNameContaining(name,paging);
        return pagedResult.getContent();

    }//showCollectionByName


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByCategory(@Size(min = 1, max = 30) String categoryName, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that Category specified by categoryName exists
        Optional<Category> result = categoryRepository.findById(categoryName);
        if(!result.isPresent())
            throw new CategoryNotFoundException();

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByCategory(result.get(), paging);
        return pagedResult.getContent();

    }//showCollectionByCategory


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByAuthor(@Size(min = 1, max = 20) String authorName, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that Author specified by authorName exists
        Optional<Author> resultAuthor = authorRepository.findById(authorName);
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByAuthor(resultAuthor.get(), paging);
        return pagedResult.getContent();
        
    }//showCollectionsByAuthor

    public void addCollection(@Valid Collection collection){

        //verify that Collection specified doesn't already exists
        if(collectionRepository.existsById(collection.getName()))
            throw new CollectionAlreadyExistsException();
        
        collectionRepository.save(collection);

    }//addCollection


    public void updateCollection(@Valid Collection collection){

        //verify that Collection specified already exists
        Optional<Collection> resultCollection = collectionRepository.findById(collection.getName());
        if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();

        //set CreationDate field, client don't know that
        collection.setCreationDate(resultCollection.get().getCreationDate());

        //merge
        collectionRepository.save(collection);

    }//updateCollection

    
    public void bindCategoryToCollection( @Size(min = 1, max = 30) String categoryName,  @Size(min = 1, max = 50) String collectionName){

        //verify that Category specified by categoryName exists
        Optional<Category> resultCategory = categoryRepository.findById(categoryName);
        if(!resultCategory.isPresent())
            throw new CategoryNotFoundException();
        
        //verify that Collection specified by collectionName exists
        Optional<Collection> resultCollection = collectionRepository.findById(collectionName);
        if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();

        //bind bidirectional relation
        resultCollection.get().bindCategory(resultCategory.get());

    }//bindCategoryToCollection
    
}//CollectionService
