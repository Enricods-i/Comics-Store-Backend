package im.enricods.ComicsStore.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.repositories.AuthorRepository;

@Service
@Transactional
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public List<Author> showAllAuthors(int pageNumber, int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findAll(paging).getContent();

    }//showAllAuthors


    public Author addAuthor(Author author){

        if(authorRepository.existsById(author.getId()))
            throw new RuntimeException(); //l'autore esiste già
        
        return authorRepository.save(author);

    }//addAuthor


    public void updateAuthor(Author author){

        if(!authorRepository.existsById(author.getId()))
            throw new RuntimeException(); //l'autore non esiste
        
        authorRepository.save(author);

    }//updateAuthor

}//AuthorService