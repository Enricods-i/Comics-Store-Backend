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
            throw new RuntimeException(); //la collezione non esiste
        
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollection(result.get(), paging);
        return pagedResult.getContent();

    }//showComicsInCollection


    @Transactional(readOnly = true)
    public List<Comic> showComicsInCollectionCreatedByAuthor(String collectionName, long authorId, int pageNumber, int pageSize, String sortBy){

        Optional<Collection> resultCollection = collectionRepository.findById(collectionName);
         if(!resultCollection.isPresent())
            throw new RuntimeException(); //la collezione non esiste
        
        Optional<Author> resultAuthor = authorRepository.findById(authorId);
        if(!resultAuthor.isPresent())
            throw new RuntimeException(); //l'autore non esiste
        
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollectionAndAuthor(resultCollection.get(), resultAuthor.get(), paging);
        return pagedResult.getContent();

    }//showComicsInCollectionCreatedByAuthor


    public Comic addComic(Comic comic, String collectionName){

        Optional<Collection> resultCollection = collectionRepository.findById(collectionName);
        if(!resultCollection.isPresent())
            throw new RuntimeException(); //la collezione non esiste
        
        Optional<Comic> resultComic = comicRepository.findByIsbn(comic.getIsbn());
        if(resultComic.isPresent())
            throw new RuntimeException(); //il fumetto esiste già
        
        Comic c = comicRepository.save(comic);
        //add comic to the collection's list
        resultCollection.get().addComic(c);
        return c;

    }//addComic


    public void updateComic(Comic comic){

        if(!comicRepository.existsById(comic.getId()))
            throw new RuntimeException(); //il fumetto non esiste
        
        //merge
        comicRepository.save(comic);

    }//updateComic


    public void addAuthorToComic(long authorId, long comicId){

        Optional<Author> resultAuthor = authorRepository.findById(authorId);
        if(!resultAuthor.isPresent())
            throw new RuntimeException(); //l'autore non esiste
        
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new RuntimeException(); //il fumetto non esiste

        resultComic.get().addAuthor(resultAuthor.get());

    }//addAuthorToComic

}//ComicService
