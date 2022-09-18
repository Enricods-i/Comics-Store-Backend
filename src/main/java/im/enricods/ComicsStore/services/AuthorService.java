package im.enricods.ComicsStore.services;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.ProblemCode;
import im.enricods.ComicsStore.utils.covers.Cover;
import im.enricods.ComicsStore.utils.covers.Type;

@Service
@Transactional
@Validated
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Transactional(readOnly = true)
    public List<Author> getAll(@Min(0) int pageNumber, @Min(0) int pageSize, @NotNull String sortBy) {

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findAll(paging).getContent();

    }// getAllAuthors

    @Transactional(readOnly = true)
    public List<Author> getByName(@NotNull @Size(min = 3, max = 20) String name, @Min(0) int pageNumber,
            @Min(0) int pageSize, @NotNull String sortBy) {

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return authorRepository.findByNameContaining(name, paging).getContent();

    }// getAuthorsByName

    public Author add(@NotNull @Valid Author author) {

        // verify that Author specified doesn't already exists
        if (authorRepository.existsByName(author.getName()))
            throw new BadRequestException(new Problem(ProblemCode.AUTHOR_ALREADY_EXISTS, "author.name"));

        author.setId(0);

        // persist
        return authorRepository.save(author);

    }// addAuthor

    @Transactional(readOnly = true)
    public void changeCover(@Min(0) long authorId, MultipartFile img) throws IOException {

        // verify that Collection specified already exists
        if (!authorRepository.existsById(authorId))
            throw new BadRequestException(new Problem(ProblemCode.AUTHOR_NOT_FOUND, "authorId"));

        Cover.save(Type.COLLECTION.getLabel() + authorId, img);

    }// changeCover

    @Transactional(readOnly = true)
    public void removeCover(@Min(0) long authorId) {

        // verify that Collection specified already exists
        if (!authorRepository.existsById(authorId))
            throw new BadRequestException(new Problem(ProblemCode.AUTHOR_NOT_FOUND, "authorId"));

        Cover.remove(Type.COLLECTION.getLabel() + authorId);

    }// removeCover

    public void remove(@Min(0) long authorId) {

        // verify that Author with authorId specified exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.AUTHOR_NOT_FOUND, "authorId"));

        Author target = auth.get();

        // unbind bidirectional relations
        Iterator<Comic> it = target.getWorks().iterator();
        while (it.hasNext()) {
            it.next().getAuthors().remove(target);
            it.remove();
        }

        authorRepository.delete(target);

    }// deleteAuthor

    public void modify(@NotNull @Valid Author author) {

        // verify that Author specified exists
        Optional<Author> auth = authorRepository.findById(author.getId());
        if (auth.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.AUTHOR_NOT_FOUND, "author.id"));

        author.setVersion(auth.get().getVersion());
        author.setCreationDate(auth.get().getCreationDate());

        // merge
        authorRepository.save(author);

    }// modify

    public void addWorks(@Min(0) long authorId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds) {

        // verify that Author with authorId specified exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.AUTHOR_NOT_FOUND, "authorId"));

        Author target = auth.get();

        Set<Comic> worksToAdd = new HashSet<>();
        Problem problemCNF = new Problem(ProblemCode.COMIC_NOT_FOUND);
        Problem problemAAIC = new Problem(ProblemCode.AUTHOR_ALREADY_IN_COMIC);

        Optional<Comic> cmc = null;
        for (long id : comicIds) {
            // verify that a Comic with id exists
            cmc = comicRepository.findById(id);
            if (cmc.isEmpty()) {
                problemCNF.add(Long.toString(id));
                continue;
            }
            if (target.getWorks().contains(cmc.get())) {
                problemAAIC.add(Long.toString(id));
                problemAAIC.add(Long.toString(authorId));
                continue;
            }
            worksToAdd.add(cmc.get());
        }

        // check if there has been any problems
        Set<Problem> problemsEncountered = new HashSet<>();
        if (!problemCNF.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCNF);
        if (!problemAAIC.getInvalidFields().isEmpty())
            problemsEncountered.add(problemAAIC);
        if (!problemsEncountered.isEmpty())
            throw new BadRequestException(problemsEncountered);

        // bind bidirectional relations with Comic
        for (Comic comic : worksToAdd)
            target.addWork(comic);

    }// addWorks

    public void removeWorks(@Min(0) long authorId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds) {

        // verify that Author with authorId specified exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.AUTHOR_NOT_FOUND, "authorId"));

        Author target = auth.get();

        Set<Comic> worksToRemove = new HashSet<>();
        Problem problemCNF = new Problem(ProblemCode.COMIC_NOT_FOUND);
        Problem problemANIC = new Problem(ProblemCode.AUTHOR_ALREADY_IN_COMIC);

        Optional<Comic> cmc = null;
        for (long id : comicIds) {
            // verify that a Comic with id exists
            cmc = comicRepository.findById(id);
            if (cmc.isEmpty()) {
                problemCNF.add(Long.toString(id));
                continue;
            }
            if (!target.getWorks().contains(cmc.get())) {
                problemANIC.add(Long.toString(id));
                problemANIC.add(Long.toString(authorId));
                continue;
            }
            worksToRemove.add(cmc.get());
        }

        // check if there has been any problems
        Set<Problem> problemsEncountered = new HashSet<>();
        if (!problemCNF.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCNF);
        if (!problemANIC.getInvalidFields().isEmpty())
            problemsEncountered.add(problemANIC);
        if (!problemsEncountered.isEmpty())
            throw new BadRequestException(problemsEncountered);

        // bind bidirectional relations with Comic
        for (Comic comic : worksToRemove)
            target.removeWork(comic);

    }// removeWorks

}// AuthorService
