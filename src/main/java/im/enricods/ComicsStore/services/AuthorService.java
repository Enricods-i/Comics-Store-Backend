package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
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
import im.enricods.ComicsStore.exceptions.AuthorAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.AuthorNotFoundException;
import im.enricods.ComicsStore.repositories.AuthorRepository;

@Service
@Transactional
@Validated
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;


    @Transactional(readOnly = true)
    public List<Author> showAllAuthors(int pageNumber, int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findAll(paging).getContent();

    }//showAllAuthors


    public void addAuthor(@Valid Author author){

        //verify that Author specified doesn't already exists
        if(authorRepository.existsById(author.getName()))
            throw new AuthorAlreadyExistsException();
        
        //persist
        authorRepository.save(author);

    }//addAuthor


    public void deleteAuthor(@NotNull @Size(min = 1, max= 20)String authorName){

        //verify that Author with authorName specified exists
        Optional<Author> resultAuthor = authorRepository.findById(authorName);
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
        Optional<Author> resultAuthor = authorRepository.findById(author.getName());
        if(!resultAuthor.isPresent())
            throw new AuthorNotFoundException();
        
        author.setCreationDate(resultAuthor.get().getCreationDate());
        System.out.println(author);
        //merge
        authorRepository.save(author);

    }//updateAuthor


}//AuthorService
