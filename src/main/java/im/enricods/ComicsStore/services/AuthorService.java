package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.utils.exceptions.AuthorAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.AuthorNotFoundException;

@Service
@Transactional
@Validated
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;


    @Transactional(readOnly = true)
    public List<Author> showAllAuthors(@Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findAll(paging).getContent();

    }//showAllAuthors


    @Transactional(readOnly = true)
    public List<Author> showAuthorsByName(@NotNull @Size(min=3, max=20) String name, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findByNameContaining(name, paging).getContent();

    }//showAuthorsByName


    public void addAuthor(@Valid Author author){

        //verify that Author specified doesn't already exists
        if(authorRepository.existsById(author.getId()))
            throw new AuthorAlreadyExistsException();
        
        //persist
        authorRepository.save(author);

    }//addAuthor


    public void deleteAuthor(@NotNull @Min(0) long authorId){

        //verify that Author with authorId specified exists
        Optional<Author> resultAuthor = authorRepository.findById(authorId);
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();
        
        Author target = resultAuthor.get();

        authorRepository.delete(target);

        //unbind the bidirectional relations
        for(Comic c : target.getWorks())
            c.getAuthors().remove(target);

    }//deleteAuthor


    public void updateAuthor(@Valid Author author){

        //verify that Author specified exists
        Optional<Author> resultAuthor = authorRepository.findById(author.getId());
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();
        
        author.setCreationDate(resultAuthor.get().getCreationDate());
        System.out.println(author);
        //merge
        authorRepository.save(author);

    }//updateAuthor


}//AuthorService
