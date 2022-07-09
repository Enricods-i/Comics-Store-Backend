package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.repositories.CategoryRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;
import im.enricods.ComicsStore.repositories.ComicRepository;

@Service
public class ManageCatalogService {
    
    @Autowired
    private ComicRepository comicRepository;
    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = false)
    public Comic addComic(Comic comic, Collection collection){

        Optional<Collection> resultCollection = collectionRepository.findById(collection.getName());
        if(!resultCollection.isPresent())
            throw new RuntimeException(); //la collezione non esiste
        
        Optional<Comic> resultComic = comicRepository.findByIsbn(comic.getIsbn());
        if(resultComic.isPresent())
            throw new RuntimeException(); //il fumetto esiste già
        
        //add comic to the collection's list
        resultCollection.get().getComics().add(comic);
        comic.setCollection(collection);
        return comicRepository.save(comic);

    }//addComic


    @Transactional(readOnly = false)
    public void addComicQuantity(int quantity, Comic comic){

        Optional<Comic> result = comicRepository.findById(comic.getId());
        if(!result.isPresent())
            throw new RuntimeException(); //il fumetto non esiste
        
        Comic c = result.get();
        int newQuantity = c.getQuantity() + quantity;
        c.setQuantity(newQuantity);

    }//addComicQuantity


    @Transactional(readOnly = false)
    public Collection addCollection(Collection collection){

        if(collectionRepository.existsById(collection.getName()))
            throw new RuntimeException(); //la collezione esiste già
        
        return collectionRepository.save(collection);

    }//addCollection

    @Transactional(readOnly = false)
    public void setCollectionPrice(float newPrice, Collection collection){

        Optional<Collection> result = collectionRepository.findById(collection.getName());
        if(!result.isPresent())
            throw new RuntimeException(); //la collezione non esiste
        
        result.get().setPrice(newPrice);

    }//setCollectionPrice


    @Transactional(readOnly = false)
    public Category addCategory(Category category){

        if(categoryRepository.existsById(category.getName()))
            throw new RuntimeException();
        
        return categoryRepository.save(category);

    }//addCategory


    @Transactional(readOnly = false)
    public void joinCategoryWithCollection(Collection collection, Category category){

        Optional<Category> resultCategory = categoryRepository.findById(category.getName());
        if(!resultCategory.isPresent())
            throw new RuntimeException(); //la categoria non esiste

        Optional<Collection> resultCollection = collectionRepository.findById(collection.getName());
        if(!resultCollection.isPresent())
            throw new RuntimeException(); //la collezione non esiste

        Collection coll = resultCollection.get();
        Category cat = resultCategory.get();
        coll.getCategories().add(cat);
        cat.getCollections().add(coll);
        
    }//joinCategoryWithCollection


    @Transactional(readOnly = true)
    public List<Category> showAllCategories(){

        return categoryRepository.findAll();
        
    }//showAllCategories


    @Transactional(readOnly = false)
    public void addDiscount(Discount discount){
        
    }//addDiscount


    public boolean finishDiscount(){
        return false;
    }


    public List<Discount> showActiveDiscounts(){
        return null;
    }


    public List<Discount> showAllDiscounts(){
        return null;
    }


    public boolean addAuthor(){
        return false;
    }

}//ManageCatalogService
