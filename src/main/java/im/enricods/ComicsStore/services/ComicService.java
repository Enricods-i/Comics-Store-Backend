package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.Discount;
import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.repositories.CartContentRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.repositories.WishListRepository;

@Service
@Transactional
@Validated
public class ComicService {
    
    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private CartContentRepository cartContentRepository;


    @Transactional(readOnly = true)
    public List<Comic> getByCollection(@NotNull @Min(0) long collectionId, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that a Collection with collectionId exists
         Optional<Collection> cllctn = collectionRepository.findById(collectionId);
         if(cllctn.isEmpty())
            throw new IllegalArgumentException("Collection "+collectionId+" not found!");
        
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollection(cllctn.get(), paging);
        return pagedResult.getContent();

    }//getByCollection


    @Transactional(readOnly = true)
    public List<Comic> getByCollectionAndAuthor(@NotNull @Min(0) long collectionId, @NotNull @Min(0) long authorId, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that a Collection with collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
         if(cllctn.isEmpty())
            throw new IllegalArgumentException("Collection "+collectionId+" not found!");
        
        //verify that a Author with authorId exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if(auth.isEmpty())
            throw new IllegalArgumentException("Author "+authorId+" not found!");
        
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollectionAndAuthor(cllctn.get(), auth.get(), paging);
        return pagedResult.getContent();

    }//getByCollectionAndAuthor


    public Comic add(@Valid Comic comic, @NotNull @Min(0) long collectionId){

        //verify that a Collection with collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if(cllctn.isEmpty())
            throw new IllegalArgumentException("Collection "+collectionId+" not found!");
        
        //verify that a Comic with specified ISBN doesn't already exists
        Optional<Comic> cmc = comicRepository.findByIsbn(comic.getIsbn());
        if(cmc.isPresent())
            throw new IllegalArgumentException("Comic with ISBN \""+comic.getIsbn()+"\" already exists!");
        
        //initialize the missing fields
        comic.setId(0);
        comic.setCollection(cllctn.get());
        
        //persist
        Comic c = comicRepository.save(comic);
    
        //bind the bidirectional relation
        cllctn.get().addComic(c);

        return c;

    }//add


    public void update(@Valid Comic comic){

        //verify that the Comic exists
        Optional<Comic> cmc = comicRepository.findById(comic.getId());
        if(cmc.isEmpty())
            throw new IllegalArgumentException("Comic "+comic.getId()+" not found!");

        //verify that if a Comic with the specified ISBN exists then it and the Comic specified must be the same
        Optional<Comic> cmc2 = comicRepository.findByIsbn(comic.getIsbn());
        if( cmc2.isPresent()  &&  !cmc.get().equals(cmc2.get())  )
            throw new IllegalArgumentException("There is already a Comic with ISBN \""+comic.getIsbn()+"\" !");
        
        //verify that a Comic with specified Number doesn't already exist in the Collection
        if( comicRepository.findByCollectionAndNumber( cmc.get().getCollection(), cmc.get().getNumber() ).isPresent())
            throw new IllegalArgumentException("A Comic with number "+cmc.get().getNumber()+" already exists in its Collection!");

        //can't change Collection or CreationDate or dateOfLastModification or Version field
        comic.setCollection(cmc.get().getCollection());
        comic.setCreationDate(cmc.get().getCreationDate());
        comic.setVersion(cmc.get().getVersion());

        //merge
        comicRepository.save(comic);

    }//update


    public void remove(@NotNull @Min(0) long comicId){

        //verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if(cmc.isEmpty())
            throw new IllegalArgumentException("Comic "+comicId+" not found!");

        Comic target = cmc.get();

        //verifiy that not exists a Purchase containing comic cmc
        if(comicRepository.existsPurchaseContaining(target))
            throw new IllegalArgumentException("Failed to remove Comic "+comicId+"; The comic was purchased!");

        //remove all relations
        target.getCollection().removeComic(target);

        for(Author auth : target.getAuthors())
            auth.removeWork(target);
        
        for(Discount disc : target.getDiscounts())
            disc.removePromotion(target);
        
        for(WishList wl : wishListRepository.findByComic(target))
            wl.getContent().remove(target);

        for(CartContent cc : cartContentRepository.findByComic(target)){
            cc.getCart().getContent().remove(cc);
            cartContentRepository.delete(cc);
        }

    }//remove


    public void addAuthors(@NotNull @Min(0) long comicId, @NotEmpty Set<@NotNull @Min(0) Long> authorIds){
        
        //verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if(cmc.isEmpty())
            throw new IllegalArgumentException("Comic "+comicId+" not found!");

        Optional<Author> auth = null;

        for(long id : authorIds){
            auth = authorRepository.findById(id);
            if(auth.isPresent())
                auth.get().addWork(cmc.get());
        }

    }//addAuthors


    public void removeAuthors(@NotNull @Min(0) long comicId, @NotEmpty Set<@NotNull @Min(0) Long> authorIds){

        //verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if(cmc.isEmpty())
            throw new IllegalArgumentException("Comic "+comicId+" not found!");

        Author author = new Author();

        for(long id : authorIds){
            author.setId(id);
            if(cmc.get().getAuthors().contains(author))
                authorRepository.findById(id).get().removeWork(cmc.get());
        }

    }//removeAuthors

}//ComicService
