package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
import im.enricods.ComicsStore.entities.CartContent;
import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.repositories.CartContentRepository;
import im.enricods.ComicsStore.repositories.CategoryRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.repositories.WishListRepository;
import im.enricods.ComicsStore.utils.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.CategoryNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.CollectionAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.CollectionNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.NonRemovalCollectionException;

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

    @Autowired
    private CartContentRepository cartContentRepository;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private ComicRepository comicRepository;
    
    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByName(@NotNull @Size(min=3, max=50) String name, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByNameContaining(name,paging);
        return pagedResult.getContent();

    }//showCollectionByName


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByCategory(@Min(0) long categoryId, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that Category specified by categoryId exists
        Optional<Category> result = categoryRepository.findById(categoryId);
        if(!result.isPresent())
            throw new CategoryNotFoundException();

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByCategory(result.get(), paging);
        return pagedResult.getContent();

    }//showCollectionByCategory


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsByAuthor(@Min(0) long authorId, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that Author specified by authorId exists
        Optional<Author> resultAuthor = authorRepository.findById(authorId);
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByAuthor(resultAuthor.get(), paging);
        return pagedResult.getContent();
        
    }//showCollectionsByAuthor


    @Transactional(readOnly = true)
    public List<Collection> showCollectionsBy(@Size(min=3, max=50) String name, @Min(0) Long categoryId, @Min(0) Long authorId, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        if( (name==null && categoryId==null) || 
            (name==null && authorId==null) || 
            (categoryId==null && authorId==null) )
            throw new IllegalArgumentException();

        Author author = null;
        if(authorId!=null){
            //verify that Author specified by authorId exists
            Optional<Author> resultAuthor = authorRepository.findById(authorId);
            if(!resultAuthor.isPresent())
                throw new AuthorNotFoundException();
            author = resultAuthor.get();
        }
        
        Category category = null;
        if(categoryId!=null){
            //verify that Category specified by categoryId exists
            Optional<Category> resultCategory = categoryRepository.findById(categoryId);
            if(!resultCategory.isPresent())
                throw new CategoryNotFoundException();
            category = resultCategory.get();
        }

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.advancedSearch(name, author, category, paging);
        return pagedResult.getContent();


    }//showCollectionsBy


    public void addCollection(@Valid Collection collection){

        //verify that Collection specified doesn't already exists
        if(collectionRepository.existsByName(collection.getName()))
            throw new CollectionAlreadyExistsException();
        
        //set fields that client don't know
        collection.setOldPrice(-1);
        collection.setVersion(1);
        collection.setImage(null);

        collectionRepository.save(collection);

    }//addCollection


    public void updateCollection(@Valid Collection collection){

        //verify that Collection specified already exists
        Optional<Collection> resultCollection = collectionRepository.findById(collection.getId());
        if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();

        Collection c = resultCollection.get();

        //client can't modify this parameters
        collection.setActualPrice(c.getActualPrice());
        collection.setOldPrice(c.getOldPrice());
        collection.setImage(c.getImage());
        collection.setVersion(c.getVersion());
        collection.setCreationDate(c.getCreationDate());

        //merge
        collectionRepository.save(collection);

    }//updateCollection


    public void changePrice(@Min(0) long collectionId, @Positive float newPrice ){

        //verify that Collection specified already exists
        Optional<Collection> resultCollection = collectionRepository.findById(collectionId);
        if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();
        
        Collection c = resultCollection.get();
        c.setOldPrice(c.getActualPrice());
        c.setActualPrice(newPrice);

    }//changePrice


    /*A Collection can be deleted if and only if there was not a Purchase that involve a Comic in the Collection considered*/
    public void deleteCollection(@Min(0) long collectionId){

        //verify that Collection specified already exists
        Optional<Collection> resultCollection = collectionRepository.findById(collectionId);
        if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();
        
        Collection target = resultCollection.get();

        //verify that there are no Purchases that involve Collection target
        if( collectionRepository.countPurchasesInCollection(target)>0 )
            throw new NonRemovalCollectionException();

        //START - remove all relations
        //remove relations with Category
        for(Category cat : target.getCategories())
            cat.getCollections().remove(target);
        
        //remove all Comics in Collection target
        for(Comic com : target.getComics()){

            //remove relation with Collection
            com.setCollection(null);

            //remove relations with Author
            for(Author auth : com.getAuthors())
                auth.getWorks().remove(com);
            
            //remove relations with Discount
            for(Discount disc : com.getDiscounts())
                disc.getComicsInPromotion().remove(com);
            
            //remove relations with CartContent
            for(CartContent cc : cartContentRepository.getCartsHavingComic(com)){
                cc.getCart().getContent().remove(cc);
                cartContentRepository.delete(cc);
            }

            //remove relations with WishList
            for(WishList wl : wishListRepository.getListsContainingComic(com))
                wl.getContent().remove(com);
            
            //delete Comic
            comicRepository.delete(com);

        }
        //END - remove all relations

        collectionRepository.delete(target);

    }//deleteCollection

    
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
