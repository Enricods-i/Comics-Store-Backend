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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import im.enricods.ComicsStore.entities.Author;
import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.repositories.AuthorRepository;
import im.enricods.ComicsStore.repositories.CategoryRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.ProblemCode;
import im.enricods.ComicsStore.utils.covers.Cover;
import im.enricods.ComicsStore.utils.covers.Type;

@Service
@Transactional
@Validated
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public List<Collection> getByName(
            @NotNull @Size(min = 3, max = 50) String name,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            @NotNull String sortBy) {

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByNameContainingIgnoreCase(name, paging);
        return pagedResult.getContent();

    }// getByName

    @Transactional(readOnly = true)
    public List<Collection> getByCategory(
            @Min(0) long categoryId,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            @NotNull String sortBy) {

        // verify that Category specified by categoryId exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.CATEGORY_NOT_FOUND, "categoryId"));

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByCategory(ctgr.get(), paging);
        return pagedResult.getContent();

    }// getByCategory

    @Transactional(readOnly = true)
    public List<Collection> getByAuthor(
            @Min(0) long authorId,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            @NotNull String sortBy) {

        // verify that Author specified by authorId exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.AUTHOR_NOT_FOUND, "authorId"));

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByAuthor(auth.get(), paging);
        return pagedResult.getContent();

    }// getByAuthor

    @Transactional(readOnly = true)
    public List<Collection> advancedSearch(
            @Size(min = 3, max = 50) String name,
            @Size(min = 3, max = 30) String categoryName,
            @Size(min = 3, max = 20) String authorName,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            @NotNull String sortBy) {

        // verify that at least two fields have been specified
        /* int count = 0; */
        if (name == null){
            /* count++; */
            name = "";
        }
        else
            name = name.toLowerCase();
        if (categoryName == null){
            /* count++; */
            categoryName = "";
        }
        else
            categoryName = categoryName.toLowerCase();
        if (authorName == null){
            /* count++; */
            authorName = "";
        }
        else
            authorName = authorName.toLowerCase();
        /* if (count >= 2)
            throw new BadRequestException(new Problem(ProblemCode.TOO_FEW_PARAMETER, "name", "categoryName", "authorName")); */

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.advancedSearch(name, authorName, categoryName, paging);
        return pagedResult.getContent();

    }// advancedSearch

    @Transactional(readOnly=true)
    public List<Collection> getRecentAdditions(){
        return collectionRepository.findTop9ByOrderByCreationDateDesc();
    }//getRecentAdditions

    public Collection add(@NotNull @Valid Collection collection) {

        // verify that Collection specified doesn't already exists
        if (collectionRepository.existsByName(collection.getName()))
        throw new BadRequestException(new Problem(ProblemCode.COLLECTION_ALREADY_EXISTS, "collection.name"));

        return collectionRepository.save(collection);

    }// add

    public void modify(@NotNull @Valid Collection collection) {

        // verify that Collection specified already exists
        Optional<Collection> c1 = collectionRepository.findById(collection.getId());
        if (c1.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_NOT_FOUND, "collection.id"));

        Collection target = c1.get();

        // verify that a Collection with the new name specified doesn't already exist
        if (!collection.getName().equals(target.getName()) && collectionRepository.existsByName(collection.getName()))
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_ALREADY_EXISTS, "collection.name"));

        collection.setVersion(target.getVersion());
        collection.setCreationDate(target.getCreationDate());

        // merge
        collectionRepository.save(collection);

    }// modify

    @Transactional(readOnly = true)
    public void changeCover(@Min(0) long collectionId, MultipartFile img) throws IOException {

        // verify that Collection specified already exists
        if (!collectionRepository.existsById(collectionId))
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_NOT_FOUND, "collectionId"));

        Cover.save(Type.COLLECTION.getLabel() + collectionId, img);

    }// changeCover

    @Transactional(readOnly = true)
    public void removeCover(@Min(0) long collectionId) {

        // verify that Collection specified already exists
        if (!collectionRepository.existsById(collectionId))
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_NOT_FOUND, "collectionId"));

        Cover.remove(Type.COLLECTION.getLabel() + collectionId);

    }// removeCover

    public void remove(@Min(0) long collectionId) {

        // verify that Collection specified already exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_NOT_FOUND, "collectionId"));

        Collection target = cllctn.get();

        // Only an empty Collection can be removed
        if (!target.getComics().isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_NOT_EMPTY, "collectionId"));

        // unbind bidirectional relations
        Iterator<Category> it = target.getCategories().iterator();
        while (it.hasNext()) {
            it.next().getCollections().remove(target);
            it.remove();
        }

        collectionRepository.delete(target);

        // remove cover
        Cover.remove(Type.COLLECTION.getLabel() + collectionId);

    }// remove

    public void bindCategories(@Min(0) long collectionId, @NotEmpty Set<@NotNull @Min(0) Long> categoryIds) {

        // verify that Collection specified by collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COLLECTION_NOT_FOUND, "collectionId"));

        Collection target = cllctn.get();

        Set<Category> categoriesToBind = new HashSet<>();
        Problem problemCNF = new Problem(ProblemCode.CATEGORY_NOT_FOUND);
        Problem problemCAIC = new Problem(ProblemCode.CATEGORY_ALREADY_IN_COLLECTION);

        Optional<Category> ctgr = null;
        for (Long id : categoryIds) {
            ctgr = categoryRepository.findById(id);
            if (ctgr.isEmpty()) {
                problemCNF.add(Long.toString(id));
                continue;
            }
            if (target.getCategories().contains(ctgr.get())) {
                problemCAIC.add(Long.toString(id));
                problemCAIC.add(Long.toString(collectionId));
                continue;
            }
            categoriesToBind.add(ctgr.get());
        }

        // check if there has been any problems
        Set<Problem> problemsEncountered = new HashSet<>();
        if (!problemCNF.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCNF);
        if (!problemCAIC.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCAIC);
        if (!problemsEncountered.isEmpty())
            throw new BadRequestException(problemsEncountered);

        // bind bidirectional relations with Category
        for (Category category : categoriesToBind)
            category.bindCollection(target);

    }// bindCategories

    public void unbindCategories(@Min(0) long collectionId, @NotEmpty Set<@NotNull @Min(0) Long> categoryIds) {

        // verify that Collection specified by collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new IllegalArgumentException("Collection " + collectionId + " not found!");

        Collection target = cllctn.get();

        Set<Category> categoriesToUnbind = new HashSet<>();
        Problem problemCNF = new Problem(ProblemCode.CATEGORY_NOT_FOUND);
        Problem problemCNIC = new Problem(ProblemCode.CATEGORY_ALREADY_IN_COLLECTION);

        Optional<Category> ctgr = null;
        for (Long id : categoryIds) {
            ctgr = categoryRepository.findById(id);
            if (ctgr.isEmpty()) {
                problemCNF.add(Long.toString(id));
                continue;
            }
            if (!target.getCategories().contains(ctgr.get())) {
                problemCNIC.add(Long.toString(id));
                problemCNIC.add(Long.toString(collectionId));
                continue;
            }
            categoriesToUnbind.add(ctgr.get());
        }

        // check if there has been any problems
        Set<Problem> problemsEncountered = new HashSet<>();
        if (!problemCNF.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCNF);
        if (!problemCNIC.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCNIC);
        if (!problemsEncountered.isEmpty())
            throw new BadRequestException(problemsEncountered);

        // bind bidirectional relations with Category
        for (Category category : categoriesToUnbind)
            category.unbindCollection(target);

    }// unbindCategories

}// CollectionService
