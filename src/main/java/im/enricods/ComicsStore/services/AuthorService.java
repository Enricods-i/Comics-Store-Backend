package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
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
import im.enricods.ComicsStore.repositories.ComicRepository;

@Service
@Transactional
@Validated
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ComicRepository comicRepository;


    @Transactional(readOnly = true)
    public List<Author> getAll(@Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findAll(paging).getContent();

    }//showAllAuthors


    @Transactional(readOnly = true)
    public List<Author> getByName(@NotNull @Size(min=3, max=20) String name, @Min(0) int pageNumber, @Min(0) int pageSize, String sortBy){

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findByNameContaining(name, paging).getContent();

    }//showAuthorsByName


    public void add(@Valid Author author){

        //verify that Author specified doesn't already exists
        if(authorRepository.existsByName(author.getName()))
            throw new IllegalArgumentException("Author with name \""+author.getName()+"\" already exists!");
        
        author.setId(0);

        //persist
        authorRepository.save(author);

    }//addAuthor


    public void remove(@NotNull @Min(0) long authorId){

        //verify that Author with authorId specified exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if(auth.isEmpty())
            throw new IllegalArgumentException("Author "+authorId+" not found!");
        
        Author target = auth.get();

        authorRepository.delete(target);

        //unbind bidirectional relations
        for(Comic c : target.getWorks())
            c.getAuthors().remove(target);

    }//deleteAuthor


    /*Allows to modify the name and the biography of the specified Author */
    public void update(@Valid Author author){

        //verify that Author specified exists
        Optional<Author> auth = authorRepository.findById(author.getId());
        if(auth.isEmpty())
            throw new IllegalArgumentException("Author "+author.getId()+" not found!");
        
        author.setVersion(auth.get().getVersion());
        author.setCreationDate(auth.get().getCreationDate());

        //merge
        authorRepository.save(author);

    }//updateAuthor


    public void addWorks(@NotNull @Min(0) long authorId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds){

        //verify that Author with authorId specified exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if(auth.isEmpty())
            throw new IllegalArgumentException("Author "+authorId+" not found!");

        Author target = auth.get();

        Optional<Comic> cmc = null;
        for(long id : comicIds){

            //verify that a Comic with id exists, if not ignore it
            cmc = comicRepository.findById(id);
            if(cmc.isPresent())
                target.addWork(cmc.get());

        }

    }//addWorks


    public void removeWorks(@NotNull @Min(0) long authorId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds){

        //verify that Author with authorId specified exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if(auth.isEmpty())
            throw new IllegalArgumentException("Author "+authorId+" not found!");

        Author target = auth.get();
        
        Optional<Comic> cmc = null;
        for(long id : comicIds){

            //verify that a Comic with id exists, if not ignore it
            cmc = comicRepository.findById(id);
            if(cmc.isPresent())
                target.removeWork(cmc.get());

        }

    }//removeWorks

}//AuthorService
