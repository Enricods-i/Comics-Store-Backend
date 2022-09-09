package im.enricods.ComicsStore.services;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.entities.Category;
import im.enricods.ComicsStore.entities.Collection;
import im.enricods.ComicsStore.repositories.CategoryRepository;
import im.enricods.ComicsStore.repositories.CollectionRepository;

@Service
@Transactional
@Validated
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Transactional(readOnly = true)
    public List<Category> getAll() {

        return categoryRepository.findAll();

    }// getAll

    @Transactional(readOnly = true)
    public List<Category> getByName(@NotNull @Size(min = 3, max = 30) String categoryName) {

        return categoryRepository.findByNameIgnoreCaseContaining(categoryName);

    }// getByName

    public Category add(@NotNull @Size(min = 1, max = 30) String categoryName) {

        // verify that Category with the name specified doesn't already exist
        if (categoryRepository.existsByName(categoryName))
            throw new IllegalArgumentException("Category with name \"" + categoryName + "\" already exists");

        Category c = new Category();
        c.setName(categoryName);

        return categoryRepository.save(c);

    }// add

    public void changeName(@NotNull @Min(0) long categoryId, @NotNull @Size(min = 3, max = 30) String newName) {

        // verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new IllegalArgumentException("Category " + categoryId + " not found!");

        if (categoryRepository.existsByName(newName))
            throw new IllegalArgumentException("Category with name \"" + newName + "\" already exists!");

        ctgr.get().setName(newName);

    }// changeName

    public void remove(@NotNull @Min(0) long categoryId) {

        // verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new IllegalArgumentException("Category " + categoryId + " not found!");

        Category target = ctgr.get();

        // unbind bidirectional relations with Collection
        Iterator<Collection> it = target.getCollections().iterator();
        while (it.hasNext()) {
            it.next().getCategories().remove(target);
            it.remove();
        }

        categoryRepository.delete(target);

    }// remove

    public void bindCollections(@NotNull @Min(0) long categoryId, @NotEmpty Set<@NotNull @Min(0) Long> collectionIds) {

        // verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new IllegalArgumentException("Category " + categoryId + " not found!");

        Category target = ctgr.get();

        Set<Collection> collectionsToBind = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Collection> cllctn = null;
        for (Long id : collectionIds) {
            // verify that Collection with current id exists
            cllctn = collectionRepository.findById(id);
            if (cllctn.isEmpty()){
                problemsEncountered.append("Collection "+id+" not found.\n");
                continue;
            }
            if (target.getCollections().contains(cllctn.get())){
                problemsEncountered.append("Collection "+id+" is already binded with category "+categoryId+".\n");
                continue;
            }
            collectionsToBind.add(cllctn.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // bind bidirectional relations with Collection
        for (Collection collection : collectionsToBind)
            target.bindCollection(collection);

    }// bindCollections

    public void unbindCollections(@NotNull @Min(0) long categoryId,
            @NotEmpty Set<@NotNull @Min(0) Long> collectionIds) {

        // verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new IllegalArgumentException("Category " + categoryId + " not found!");

        Category target = ctgr.get();

        Set<Collection> collectionsToUnbind = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Collection> cllctn = null;
        for (Long id : collectionIds) {
            // verify that Collection with current id exists
            cllctn = collectionRepository.findById(id);
            if (cllctn.isEmpty()){
                problemsEncountered.append("Collection "+id+" not found.\n");
                continue;
            }
            if (!target.getCollections().contains(cllctn.get())){
                problemsEncountered.append("Collection "+id+" is not binded with category "+categoryId+".\n");
                continue;
            }
            collectionsToUnbind.add(cllctn.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // bind bidirectional relations with Collection
        for (Collection collection : collectionsToUnbind)
            target.unbindCollection(collection);

    }// unbindCollections

}// CategoryService
