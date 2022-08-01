package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.repositories.CategoryRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;
import im.enricods.ComicsStore.utils.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.CategoryNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.CollectionAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.CollectionNotFoundException;

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
    public List<Collection> showCollectionsByName(@NotNull @Size(min = 1, max = 50) String name, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByNameContaining(name,paging);
        return pagedResult.getContent();

    }//showCollectionByName


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByCategory(@NotNull @Min(0) long categoryId, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that Category specified by categoryId exists
        Optional<Category> result = categoryRepository.findById(categoryId);
        if(!result.isPresent())
            throw new CategoryNotFoundException();

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByCategory(result.get(), paging);
        return pagedResult.getContent();

    }//showCollectionByCategory


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByAuthor(@NotNull @Min(0) long authorId, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that Author specified by authorId exists
        Optional<Author> resultAuthor = authorRepository.findById(authorId);
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByAuthor(resultAuthor.get(), paging);
        return pagedResult.getContent();
        
    }//showCollectionsByAuthor

    public void addCollection(@Valid Collection collection){

        //verify that Collection specified doesn't already exists
        if(collectionRepository.existsByName(collection.getName()))
            throw new CollectionAlreadyExistsException();
        
        collectionRepository.save(collection);

    }//addCollection


    public void updateCollection(@Valid Collection collection){

        //verify that Collection specified already exists
        Optional<Collection> resultCollection = collectionRepository.findById(collection.getId());
        if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();

        //set CreationDate field, client don't know that
        collection.setCreationDate(resultCollection.get().getCreationDate());

        //merge
        collectionRepository.save(collection);

    }//updateCollection

    
    public void bindCategoryToCollection(@NotNull @Min(0) long categoryId, @NotNull  @Min(0) long collectionId){

        //verify that Category specified by categoryId exists
        Optional<Category> resultCategory = categoryRepository.findById(categoryId);
        if(!resultCategory.isPresent())
            throw new CategoryNotFoundException();
        
        //verify that Collection specified by collectionId exists
        Optional<Collection> resultCollection = collectionRepository.findById(collectionId);
        if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();

        //bind bidirectional relation
        resultCollection.get().bindCategory(resultCategory.get());

    }//bindCategoryToCollection
    
}//CollectionService
