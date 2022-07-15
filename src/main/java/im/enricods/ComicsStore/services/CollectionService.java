package im.enricods.ComicsStore.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.repositories.CategoryRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;

@Service
@Transactional
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthorRepository authorRepository;
    
    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByName(String name, int pageNumber, int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByNameContaining(name,paging);
        return pagedResult.getContent();

    }//showCollectionByName


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByCategory(String categoryName, int pageNumber, int pageSize, String sortBy){

        Optional<Category> result = categoryRepository.findById(categoryName);
        if(!result.isPresent())
            throw new RuntimeException(); //la categoria non esiste

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByCategory(result.get(), paging);
        return pagedResult.getContent();

    }//showCollectionByCategory


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByAuthor(Author author, int pageNumber, int pageSize, String sortBy){

        if(!authorRepository.existsById(author.getId()))
            throw new RuntimeException(); //l'autore non esiste

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByAuthor(author, paging);
        return pagedResult.getContent();
        
    }//showCollectionsByAuthor


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsInPeriod(Date startDate, Date endDate, int pageNumber, int pageSize, String sortBy){

        if ( startDate.compareTo(endDate) >= 0 ) 
            throw new RuntimeException(); //la end date è più piccola della start
        
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByFirstReleaseBetween(startDate, endDate, paging);
        return pagedResult.getContent();

    }//showCollectionsInPeriod


    public Collection addCollection(Collection collection){

        if(collectionRepository.existsById(collection.getName()))
            throw new RuntimeException(); //la collezione esiste già
        
        return collectionRepository.save(collection);

    }//addCollection


    public void updateCollection(Collection collection){

        if(!collectionRepository.existsById(collection.getName()))
            throw new RuntimeException(); //la collezione non esiste

        //merge
        collectionRepository.save(collection);

    }//updateCollection

    
    public void bindCategoryToCollection(String categoryName, String collectionName){

        Optional<Category> resultCategory = categoryRepository.findById(categoryName);
        if(!resultCategory.isPresent())
            throw new RuntimeException(); //la categoria non esiste
        
        Optional<Collection> resultCollection = collectionRepository.findById(collectionName);
        if(!resultCollection.isPresent())
            throw new RuntimeException(); //la collezione non esiste

        resultCollection.get().bindCategory(resultCategory.get());

    }//bindCategoryToCollection
    
}//CollectionService
