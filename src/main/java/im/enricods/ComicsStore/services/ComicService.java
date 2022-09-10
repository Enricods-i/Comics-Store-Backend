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
    public List<Comic> getByCollection(@Min(0) long collectionId, @Min(0) int pageNumber, @Min(0) int pageSize,
            @NotNull String sortBy) {

        // verify that a Collection with collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new IllegalArgumentException("Collection " + collectionId + " not found.");

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollection(cllctn.get(), paging);
        return pagedResult.getContent();

    }// getByCollection

    // for the research by author
    @Transactional(readOnly = true)
    public List<Comic> getByCollectionAndAuthor(@Min(0) long collectionId, @Min(0) long authorId,
            @Min(0) int pageNumber, @Min(0) int pageSize, @NotNull String sortBy) {

        // verify that a Collection with collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new IllegalArgumentException("Collection " + collectionId + " not found.");

        // verify that a Author with authorId exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new IllegalArgumentException("Author " + authorId + " not found.");

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollectionAndAuthor(cllctn.get(), auth.get(), paging);
        return pagedResult.getContent();

    }// getByCollectionAndAuthor

    public Comic add(@Min(0) long collectionId, @NotNull @Valid Comic comic) {

        // verify that a Collection with collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new IllegalArgumentException("Collection " + collectionId + " not found.");

        // verify that a Comic with specified ISBN doesn't already exist
        if (comicRepository.existsByIsbn(comic.getIsbn()))
            throw new IllegalArgumentException("Comic with ISBN \"" + comic.getIsbn() + "\" already exists.");

        // verify that a Comic with specified Number doesn't already exist in the
        // Collection specified by collectionId
        if (comicRepository.existsByCollectionAndNumber(cllctn.get(), comic.getNumber()))
            throw new IllegalArgumentException("Comic with number " + comic.getNumber()
                    + " already exists in the collection " + collectionId + ".");

        comic.setId(0);

        // bind the bidirectional relation
        cllctn.get().addComic(comic);

        // persist
        return comicRepository.save(comic);

    }// add

    public void update(@NotNull @Valid Comic comic) {

        // verify that the Comic exists
        Optional<Comic> cmc = comicRepository.findById(comic.getId());
        if (cmc.isEmpty())
            throw new IllegalArgumentException("Comic " + comic.getId() + " not found.");

        // verify that if a Comic with the specified ISBN exists then it and the Comic
        // specified must be the same
        if (!cmc.get().getIsbn().equals(comic.getIsbn()) && comicRepository.existsByIsbn(comic.getIsbn()))
            throw new IllegalArgumentException("There is already a Comic with ISBN \"" + comic.getIsbn() + "\".");

        // verify that a Comic with specified Number doesn't already exist in the
        // Collection
        if (cmc.get().getNumber() != comic.getNumber()
                && comicRepository.existsByCollectionAndNumber(cmc.get().getCollection(), comic.getNumber()))
            throw new IllegalArgumentException(
                    "A Comic with number " + cmc.get().getNumber() + " already exists in the collection "
                            + comic.getCollection() + ".");

        // can't change Collection or CreationDate
        comic.setVersion(cmc.get().getVersion());
        comic.setCollection(cmc.get().getCollection());
        comic.setCreationDate(cmc.get().getCreationDate());

        // merge
        comicRepository.save(comic);

    }// update

    public void remove(@Min(0) long comicId) {

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new IllegalArgumentException("Comic " + comicId + " not found.");

        Comic target = cmc.get();

        // verifiy that not exists a Purchase containing comic cmc
        if (comicRepository.existsPurchaseContaining(target))
            throw new IllegalArgumentException("Failed to remove comic " + comicId + ": the comic was purchased.");

        // remove all relations

        // with collection
        target.getCollection().removeComic(target);

        // with author
        Iterator<Author> it = target.getAuthors().iterator();
        while (it.hasNext()) {
            it.next().getWorks().remove(target);
            it.remove();
        }

        // with discount
        Iterator<Discount> it1 = target.getDiscounts().iterator();
        while (it1.hasNext()) {
            it1.next().getComicsInPromotion().remove(target);
            it1.remove();
        }

        // with wish list
        for (WishList wl : wishListRepository.findByComic(target))
            wl.getContent().remove(target);

        // with cart content
        for (CartContent cc : cartContentRepository.findByComic(target)) {
            cc.getCart().getContent().remove(cc);
            cartContentRepository.delete(cc);
        }

        comicRepository.delete(target);

    }// remove

    public void addAuthors(@Min(0) long comicId, @NotEmpty Set<@NotNull @Min(0) Long> authorIds) {

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new IllegalArgumentException("Comic " + comicId + " not found!");

        Comic target = cmc.get();

        Set<Author> authorsToAdd = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Author> auth = null;
        for (long id : authorIds) {
            // verify that an Author with id exists
            auth = authorRepository.findById(id);
            if (auth.isEmpty()) {
                problemsEncountered.append("Author " + id + " not found.\n");
                continue;
            }
            if (target.getAuthors().contains(auth.get())) {
                problemsEncountered.append("Author " + id + " is already linked to the comic " + comicId + ".\n");
                continue;
            }
            authorsToAdd.add(auth.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // bind bidirectional relations with Author
        for (Author author : authorsToAdd)
            author.addWork(target);

    }// addAuthors

    public void removeAuthors(@Min(0) long comicId, @NotEmpty Set<@NotNull @Min(0) Long> authorIds) {

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new IllegalArgumentException("Comic " + comicId + " not found!");

        Comic target = cmc.get();

        Set<Author> authorsToRemove = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Author> auth = null;
        for (long id : authorIds) {
            // verify that an Author with id exists
            auth = authorRepository.findById(id);
            if (auth.isEmpty()) {
                problemsEncountered.append("Author " + id + " not found.\n");
                continue;
            }
            if (!target.getAuthors().contains(auth.get())) {
                problemsEncountered.append("Author " + id + " is not linked to the comic " + comicId + ".\n");
                continue;
            }
            authorsToRemove.add(auth.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // bind bidirectional relations with Author
        for (Author author : authorsToRemove)
            author.removeWork(target);

    }// removeAuthors

}// ComicService
