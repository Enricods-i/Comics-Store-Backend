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
    public List<Collection> getByName(@NotNull @Size(min = 3, max = 50) String name,
            @Min(0) int pageNumber,
            @Min(0) int pageSize, String sortBy) {

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByNameContainingIgnoreCase(name, paging);
        return pagedResult.getContent();

    }// getByName

    @Transactional(readOnly = true)
    public List<Collection> getByCategory(@Min(0) long categoryId,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            String sortBy) {

        // verify that Category specified by categoryId exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new IllegalArgumentException("Category " + categoryId + " not found.");

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByCategory(ctgr.get(), paging);
        return pagedResult.getContent();

    }// getByCategory

    @Transactional(readOnly = true)
    public List<Collection> getByAuthor(@Min(0) long authorId,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            String sortBy) {

        // verify that Author specified by authorId exists
        Optional<Author> auth = authorRepository.findById(authorId);
        if (auth.isEmpty())
            throw new IllegalArgumentException("Author " + authorId + " not found.");

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.findByAuthor(auth.get(), paging);
        return pagedResult.getContent();

    }// getByAuthor

    @Transactional(readOnly = true)
    public List<Collection> advancedSearch(@Size(min = 3, max = 50) String name,
            @Min(0) Long categoryId,
            @Min(0) Long authorId,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            String sortBy) {

        // verify that at least two fields have been specified
        int count = 0;
        if (name == null)
            count++;
        if (categoryId == null)
            count++;
        if (authorId == null)
            count++;
        if (count >= 2)
            throw new IllegalArgumentException("Too many parameter are null.");

        // verify that Author specified by authorId exists
        Author author = null;
        if (authorId != null) {
            Optional<Author> auth = authorRepository.findById(authorId);
            if (auth.isEmpty())
                throw new IllegalArgumentException("Author " + authorId + " not found.");
            author = auth.get();
        }

        // verify that Category specified by categoryId exists
        Category category = null;
        if (categoryId != null) {
            Optional<Category> ctgr = categoryRepository.findById(categoryId);
            if (ctgr.isEmpty())
                throw new IllegalArgumentException("Category " + categoryId + " not found.");
            category = ctgr.get();
        }

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Collection> pagedResult = collectionRepository.advancedSearch(name, author, category, paging);
        return pagedResult.getContent();

    }// advancedSearch

    public Collection add(@Valid Collection collection) {

        // verify that Collection specified doesn't already exists
        if (collectionRepository.existsByName(collection.getName()))
            throw new IllegalArgumentException("Collection with name \"" + collection.getName() + "\" already exists.");

        return collectionRepository.save(collection);

    }// add

    public void modify(@Valid Collection collection) {

        // verify that Collection specified already exists
        Optional<Collection> c1 = collectionRepository.findById(collection.getId());
        if (c1.isEmpty())
            throw new IllegalArgumentException("Collection " + collection.getId() + " not found.");

        Collection target = c1.get();

        // verify that a Collection with the new name specified doesn't already exist
        if (!collection.getName().equals(target.getName()) && collectionRepository.existsByName(collection.getName()))
            throw new IllegalArgumentException(
                    "A Collection with name \"" + collection.getName() + "\" already exists.");

        collection.setVersion(target.getVersion());
        collection.setCreationDate(target.getCreationDate());

        // merge
        collectionRepository.save(collection);

    }// modify

    @Transactional(readOnly = true)
    public void changeCover(@Min(0) long collectionId, MultipartFile img) throws IOException {

        // verify that Collection specified already exists
        if (!collectionRepository.existsById(collectionId))
            throw new IllegalArgumentException("Collection " + collectionId + " not found.");

        Cover.save(Type.COLLECTION.getLabel() + collectionId, img);

    }// changeCover

    @Transactional(readOnly = true)
    public void removeCover(@Min(0) long collectionId) {

        // verify that Collection specified already exists
        if (!collectionRepository.existsById(collectionId))
            throw new IllegalArgumentException("Collection " + collectionId + " not found.");

        Cover.remove(Type.COLLECTION.getLabel() + collectionId);

    }// removeCover

    public void remove(@Min(0) long collectionId) {

        // verify that Collection specified already exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new IllegalArgumentException("Collection " + collectionId + " not found.");

        Collection target = cllctn.get();

        // Only an empty Collection can be removed
        if (!target.getComics().isEmpty())
            throw new IllegalArgumentException("Collection " + collectionId + " is not empty.");

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

    public void bindCategories(@NotNull @Min(0) long collectionId, @NotEmpty Set<@NotNull @Min(0) Long> categoryIds) {

        // verify that Collection specified by collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new IllegalArgumentException("Collection " + collectionId + " not found!");

        Collection target = cllctn.get();

        Set<Category> categoriesToBind = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Category> ctgr = null;
        for (Long id : categoryIds) {
            ctgr = categoryRepository.findById(id);
            if (ctgr.isEmpty()) {
                problemsEncountered.append("Category " + id + " not found.\n");
                continue;
            }
            if (target.getCategories().contains(ctgr.get())) {
                problemsEncountered
                        .append("Category " + id + " already binded to the collection " + collectionId + ".\n");
                continue;
            }
            categoriesToBind.add(ctgr.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // bind bidirectional relations with Category
        for (Category category : categoriesToBind)
            category.bindCollection(target);

    }// bindCategories

    public void unbindCategories(@NotNull @Min(0) long collectionId, @NotEmpty Set<@NotNull @Min(0) Long> categoryIds) {

        // verify that Collection specified by collectionId exists
        Optional<Collection> cllctn = collectionRepository.findById(collectionId);
        if (cllctn.isEmpty())
            throw new IllegalArgumentException("Collection " + collectionId + " not found!");

        Collection target = cllctn.get();

        Set<Category> categoriesToUnbind = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Category> ctgr = null;
        for (Long id : categoryIds) {
            ctgr = categoryRepository.findById(id);
            if (ctgr.isEmpty()) {
                problemsEncountered.append("Category " + id + " not found.\n");
                continue;
            }
            if (!target.getCategories().contains(ctgr.get())) {
                problemsEncountered.append("Category " + id + " not binded to the collection " + collectionId + ".\n");
                continue;
            }
            categoriesToUnbind.add(ctgr.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // bind bidirectional relations with Category
        for (Category category : categoriesToUnbind)
            category.unbindCollection(target);

    }// unbindCategories

}// CollectionService
