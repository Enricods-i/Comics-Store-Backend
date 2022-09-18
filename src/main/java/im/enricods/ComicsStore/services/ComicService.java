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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

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
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.ProblemCode;
import im.enricods.ComicsStore.utils.covers.Cover;
import im.enricods.ComicsStore.utils.covers.Type;

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
    public List<Comic> getByCollection(
            @Min(0) long collectionId,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            @NotNull String sortBy) {

        // verify that a Collection with collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_NOT_FOUND, "collectionId"));

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollection(cllctn.get(), paging);
        return pagedResult.getContent();

    }// getByCollection

    // for the research by author
    @Transactional(readOnly = true)
    public List<Comic> getByCollectionAndAuthor(
            @Min(0) long collectionId,
            @Min(0) long authorId,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            @NotNull String sortBy) {

        // verify that a Collection with collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_NOT_FOUND, "collectionId"));

        // verify that a Author with authorId exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.AUTHOR_NOT_FOUND, "authorId"));

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = comicRepository.findByCollectionAndAuthor(cllctn.get(), auth.get(), paging);
        return pagedResult.getContent();

    }// getByCollectionAndAuthor

    public Comic add(@Min(0) long collectionId, @NotNull @Valid Comic comic) {

        // verify that a Collection with collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_NOT_FOUND, "collectionId"));

        // verify that a Comic with specified ISBN doesn't already exist
        if (comicRepository.existsByIsbn(comic.getIsbn()))
            throw new BadRequestException(new Problem(ProblemCode.COMIC_ALREADY_EXISTS, "comic.isbn"));

        // verify that a Comic with specified Number doesn't already exist in the
        // Collection specified by collectionId
        if (comicRepository.existsByCollectionAndNumber(cllctn.get(), comic.getNumber()))
            throw new BadRequestException(new Problem(ProblemCode.COMIC_ALREADY_EXISTS, "comic.number", "collectionID"));

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
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND, "comic.id"));

        // verify that if a Comic with the specified ISBN exists then it and the Comic
        // specified must be the same
        if (!cmc.get().getIsbn().equals(comic.getIsbn()) && comicRepository.existsByIsbn(comic.getIsbn()))
            throw new BadRequestException(new Problem(ProblemCode.COMIC_ALREADY_EXISTS, "comic.isbn"));

        // verify that a Comic with specified Number doesn't already exist in the
        // Collection
        if (cmc.get().getNumber() != comic.getNumber()
                && comicRepository.existsByCollectionAndNumber(cmc.get().getCollection(), comic.getNumber()))
                throw new BadRequestException(new Problem(ProblemCode.COMIC_ALREADY_EXISTS, "comic.number", "comic.collection"));

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
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND, "comicId"));

        Comic target = cmc.get();

        // verifiy that not exists a Purchase containing comic cmc
        if (comicRepository.existsPurchaseContaining(target))
            throw new BadRequestException(new Problem(ProblemCode.COMIC_PURCHASED, "comicId"));

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
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND, "comicId"));

        Comic target = cmc.get();

        Set<Author> authorsToAdd = new HashSet<>();
        Problem problemANF = new Problem(ProblemCode.AUTHOR_NOT_FOUND);
        Problem problemAAIC = new Problem(ProblemCode.AUTHOR_ALREADY_IN_COMIC);

        Optional<Author> auth = null;
        for (long id : authorIds) {
            // verify that an Author with id exists
            auth = authorRepository.findById(id);
            if (auth.isEmpty()) {
                problemANF.add(Long.toString(id));
                continue;
            }
            if (target.getAuthors().contains(auth.get())) {
                problemAAIC.add(Long.toString(id));
                problemAAIC.add(Long.toString(comicId));
                continue;
            }
            authorsToAdd.add(auth.get());
        }

        // check if there has been any problems
        Set<Problem> problemsEncountered = new HashSet<>();
        if (!problemANF.getInvalidFields().isEmpty())
            problemsEncountered.add(problemANF);
        if (!problemAAIC.getInvalidFields().isEmpty())
            problemsEncountered.add(problemAAIC);
        if (!problemsEncountered.isEmpty())
            throw new BadRequestException(problemsEncountered);

        // bind bidirectional relations with Author
        for (Author author : authorsToAdd)
            author.addWork(target);

    }// addAuthors

    @Transactional(readOnly = true)
    public void changeCover(@Min(0) long comicId, MultipartFile img) throws IOException {

        // verify that Comic specified already exists
        if (!comicRepository.existsById(comicId))
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND, "comicId"));

        Cover.save(Type.COMIC.getLabel() + comicId, img);

    }// changeCover

    @Transactional(readOnly = true)
    public void removeCover(@Min(0) long comicId){

        // verify that Comic specified already exists
        if (!comicRepository.existsById(comicId))
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND, "comicId"));

        Cover.remove(Type.COMIC.getLabel() + comicId);

    }// changeCover

    public void removeAuthors(@Min(0) long comicId, @NotEmpty Set<@NotNull @Min(0) Long> authorIds) {

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND, "comicId"));

        Comic target = cmc.get();

        Set<Author> authorsToRemove = new HashSet<>();
        Problem problemANF = new Problem(ProblemCode.AUTHOR_NOT_FOUND);
        Problem problemANIC = new Problem(ProblemCode.AUTHOR_NOT_IN_COMIC);

        Optional<Author> auth = null;
        for (long id : authorIds) {
            // verify that an Author with id exists
            auth = authorRepository.findById(id);
            if (auth.isEmpty()) {
                problemANF.add(Long.toString(id));
                continue;
            }
            if (!target.getAuthors().contains(auth.get())) {
                problemANIC.add(Long.toString(id));
                problemANIC.add(Long.toString(comicId));
                continue;
            }
            authorsToRemove.add(auth.get());
        }

        // check if there has been any problems
        Set<Problem> problemsEncountered = new HashSet<>();
        if (!problemANF.getInvalidFields().isEmpty())
            problemsEncountered.add(problemANF);
        if (!problemANIC.getInvalidFields().isEmpty())
            problemsEncountered.add(problemANIC);
        if (!problemsEncountered.isEmpty())
            throw new BadRequestException(problemsEncountered);

        // bind bidirectional relations with Author
        for (Author author : authorsToRemove)
            author.removeWork(target);

    }// removeAuthors

}// ComicService
