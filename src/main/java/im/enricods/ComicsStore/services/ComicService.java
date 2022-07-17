package im.enricods.ComicsStore.services;

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
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.exceptions.CollectionNotFoundException;
import im.enricods.ComicsStore.exceptions.ComicAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;
import im.enricods.ComicsStore.repositories.ComicRepository;

@Service
@Transactional
public class ComicService {
    
    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private AuthorRepository authorRepository;


    @Transactional(readOnly = true)
    public List<Comic> showComicsInCollection(String collectionName, int pageNumber, int pageSize, String sortBy){

         Optional<Collection> result = collectionRepository.findById(collectionName);
         if(!result.isPresent())
            throw new CollectionNotFoundException();
        
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollection(result.get(), paging);
        return pagedResult.getContent();

    }//showComicsInCollection


    @Transactional(readOnly = true)
    public List<Comic> showComicsInCollectionCreatedByAuthor(String collectionName, String authorName, int pageNumber, int pageSize, String sortBy){

        Optional<Collection> resultCollection = collectionRepository.findById(collectionName);
         if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();
        
        Optional<Author> resultAuthor = authorRepository.findById(authorName);
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();
        
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollectionAndAuthor(resultCollection.get(), authorName, paging);
        return pagedResult.getContent();

    }//showComicsInCollectionCreatedByAuthor


    public Comic addComic(Comic comic, String collectionName){

        Optional<Collection> resultCollection = collectionRepository.findById(collectionName);
        if(!resultCollection.isPresent())
            throw new CollectionNotFoundException();
        
        Optional<Comic> resultComic = comicRepository.findByIsbn(comic.getIsbn());
        if(resultComic.isPresent())
            throw new ComicAlreadyExistsException();
        
        Comic c = comicRepository.save(comic);
    
        resultCollection.get().addComic(c);

        return c;

    }//addComic


    public void updateComic(Comic comic){

        if(!comicRepository.existsById(comic.getId()))
            throw new ComicNotFoundException();
        
        //merge
        comicRepository.save(comic);

    }//updateComic


    public void addAuthorToComic(String authorName, long comicId){

        Optional<Author> resultAuthor = authorRepository.findById(authorName);
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();
        
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();

        resultComic.get().addAuthor(resultAuthor.get());

    }//addAuthorToComic

}//ComicService
