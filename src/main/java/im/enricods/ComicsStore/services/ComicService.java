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
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.utils.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.CollectionNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.ComicAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.ComicNotFoundException;

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


    @Transactional(readOnly = true)
    public List<Comic> showComicsInCollection(@NotNull @Size(min = 1, max = 50) String collectionName, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that a Collection with collectionName exists
         Optional<Collection> result = collectionRepository.findById(collectionName);
         if(!result.isPresent())
            throw new CollectionNotFoundException();
        
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollection(result.get(), paging);
        return pagedResult.getContent();

    }//showComicsInCollection


    @Transactional(readOnly = true)
    public List<Comic> showComicsInCollectionCreatedByAuthor(@Size(min = 1, max = 50) String collectionName, @Size(min = 1, max = 20) String authorName, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        //verify that a Collection with collectionName exists
        Optional<Collection> resultCollection = collectionRepository.findById(collectionName);
         if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();
        
        //verify that a Author with authorName exists
        Optional<Author> resultAuthor = authorRepository.findById(authorName);
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();
        
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollectionAndAuthor(resultCollection.get(), resultAuthor.get(), paging);
        return pagedResult.getContent();

    }//showComicsInCollectionCreatedByAuthor


    public Comic addComic(@Valid Comic comic, @Size(min = 1, max = 50) String collectionName){

        //verify that a Collection with collectionName exists
        Optional<Collection> resultCollection = collectionRepository.findById(collectionName);
        if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();
        
        //verify that a Comic with specified ISBN doesn't already exists and verify that no Id has been specified
        Optional<Comic> resultComic = comicRepository.findByIsbn(comic.getIsbn());
        if(comic.getId()!=0 || resultComic.isPresent())
            throw new ComicAlreadyExistsException();
        
        //initialize the missing fields
        comic.setCollection(resultCollection.get());
        comic.setVersion(1);
        
        //persist
        Comic c = comicRepository.save(comic);
    
        //bind the bidirectional relation
        resultCollection.get().addComic(c);

        return c;

    }//addComic


    public void updateComic(@Valid Comic comic){

        //verify that the Comic exists
        Optional<Comic> resultComic = comicRepository.findById(comic.getId());
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();

        //verify that if a Comic with the specified ISBN exists then it and the Comic specified must be the same
        if( comicRepository.existsByIsbn(comic.getIsbn()) && !resultComic.get().equals( comicRepository.findByIsbn(comic.getIsbn()).get() )  )
            throw new ComicAlreadyExistsException();
        
        //verify that a Comic with specified Number doesn't already exists in the Collection
        if( comicRepository.findByCollectionAndNumber( resultComic.get().getCollection(), resultComic.get().getNumber() ).isPresent())
            throw new ComicAlreadyExistsException();

        //can't change Collection or CreationDate or dateOfLastModification or Version field
        comic.setCollection(resultComic.get().getCollection());
        comic.setCreationDate(resultComic.get().getCreationDate());
        comic.setVersion(resultComic.get().getVersion());

        //merge
        comicRepository.save(comic);

    }//updateComic


    public void addAuthorToComic(@Size(min = 1, max = 20) String authorName, @Min(1) long comicId){

        //verify that a Author with authorName exists
        Optional<Author> resultAuthor = authorRepository.findById(authorName);
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();
        
        //verify that a Comic with comicId exists
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();

        //bind the bidirectional relation
        resultComic.get().addAuthor(resultAuthor.get());

    }//addAuthorToComic

}//ComicService
