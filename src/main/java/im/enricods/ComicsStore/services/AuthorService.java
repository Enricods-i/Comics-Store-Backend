package im.enricods.ComicsStore.services;

import java.util.HashSet;
import java.util.Iterator;
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
    public List<Author> getAll(@Min(0) int pageNumber, @Min(0) int pageSize, String sortBy) {

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findAll(paging).getContent();

    }// showAllAuthors

    @Transactional(readOnly = true)
    public List<Author> getByName(@NotNull @Size(min = 3, max = 20) String name, @Min(0) int pageNumber,
            @Min(0) int pageSize, String sortBy) {

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findByNameContaining(name, paging).getContent();

    }// showAuthorsByName

    public Author add(@Valid Author author) {

        // verify that Author specified doesn't already exists
        if (authorRepository.existsByName(author.getName()))
            throw new IllegalArgumentException("Author with name \"" + author.getName() + "\" already exists!");

        author.setId(0);

        // persist
        return authorRepository.save(author);

    }// addAuthor

    public void remove(@NotNull @Min(0) long authorId) {

        // verify that Author with authorId specified exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new IllegalArgumentException("Author " + authorId + " not found!");

        Author target = auth.get();

        // unbind bidirectional relations
        Iterator<Comic> it = target.getWorks().iterator();
        while (it.hasNext()) {
            it.next().getAuthors().remove(target);
            it.remove();
        }

        authorRepository.delete(target);

    }// deleteAuthor

    public void modify(@Valid Author author) {

        // verify that Author specified exists
        Optional<Author> auth = authorRepository.findById(author.getId());
        if (auth.isEmpty())
            throw new IllegalArgumentException("Author " + author.getId() + " not found!");

        author.setVersion(auth.get().getVersion());
        author.setCreationDate(auth.get().getCreationDate());

        // merge
        authorRepository.save(author);

    }// modify

    public void addWorks(@NotNull @Min(0) long authorId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds) {

        // verify that Author with authorId specified exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new IllegalArgumentException("Author " + authorId + " not found!");

        Author target = auth.get();

        Set<Comic> worksToAdd = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Comic> cmc = null;
        for (long id : comicIds) {
            // verify that a Comic with id exists
            cmc = comicRepository.findById(id);
            if (cmc.isEmpty())
                problemsEncountered.append("Comic " + id + " not found.\n");

            worksToAdd.add(cmc.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // bind bidirectional relations with Comic
        for (Comic comic : worksToAdd)
            target.addWork(comic);

    }// addWorks

    public void removeWorks(@NotNull @Min(0) long authorId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds) {

        // verify that Author with authorId specified exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new IllegalArgumentException("Author " + authorId + " not found!");

        Author target = auth.get();

        Set<Comic> worksToRemove = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Comic> cmc = null;
        for (long id : comicIds) {
            // verify that a Comic with id exists
            cmc = comicRepository.findById(id);
            if (cmc.isEmpty())
                problemsEncountered.append("Comic " + id + " not found.\n");
            if (!target.getWorks().contains(cmc.get()))
                problemsEncountered.append("Comic "+id+"does not belong to the work of the author "+authorId);
            worksToRemove.add(cmc.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // bind bidirectional relations with Comic
        for (Comic comic : worksToRemove)
            target.removeWork(comic);

    }// removeWorks

}// AuthorService
