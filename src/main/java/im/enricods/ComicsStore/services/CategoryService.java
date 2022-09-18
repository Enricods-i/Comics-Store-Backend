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
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.ProblemCode;

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
            throw new BadRequestException(new Problem(ProblemCode.CATEGORY_ALREADY_EXISTS, "categoryName"));

        Category c = new Category();
        c.setName(categoryName);

        return categoryRepository.save(c);

    }// add

    public void changeName(@Min(0) long categoryId, @NotNull @Size(min = 3, max = 30) String newName) {

        // verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.CATEGORY_NOT_FOUND, "categoryId"));

        if (categoryRepository.existsByName(newName))
            throw new BadRequestException(new Problem(ProblemCode.CATEGORY_ALREADY_EXISTS, "newName"));

        ctgr.get().setName(newName);

    }// changeName

    public void remove(@Min(0) long categoryId) {

        // verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.CATEGORY_NOT_FOUND, "categoryId"));

        Category target = ctgr.get();

        // unbind bidirectional relations with Collection
        Iterator<Collection> it = target.getCollections().iterator();
        while (it.hasNext()) {
            it.next().getCategories().remove(target);
            it.remove();
        }

        categoryRepository.delete(target);

    }// remove

    public void bindCollections(@Min(0) long categoryId, @NotEmpty Set<@NotNull @Min(0) Long> collectionIds) {

        // verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.CATEGORY_NOT_FOUND, "categoryId"));

        Category target = ctgr.get();

        Set<Collection> collectionsToBind = new HashSet<>();
        Problem problemCNF = new Problem(ProblemCode.COLLECTION_NOT_FOUND);
        Problem problemCAIC = new Problem(ProblemCode.CATEGORY_ALREADY_IN_COLLECTION);

        Optional<Collection> cllctn = null;
        for (Long id : collectionIds) {
            // verify that Collection with current id exists
            cllctn = collectionRepository.findById(id);
            if (cllctn.isEmpty()) {
                problemCNF.add(Long.toString(id));
                continue;
            }
            if (target.getCollections().contains(cllctn.get())) {
                problemCAIC.add(Long.toString(id));
                problemCAIC.add(Long.toString(categoryId));
                continue;
            }
            collectionsToBind.add(cllctn.get());
        }

        // check if there has been any problems
        Set<Problem> problemsEncountered = new HashSet<>();
        if (!problemCNF.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCNF);
        if (!problemCAIC.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCAIC);
        if (!problemsEncountered.isEmpty())
            throw new BadRequestException(problemsEncountered);

        // bind bidirectional relations with Collection
        for (Collection collection : collectionsToBind)
            target.bindCollection(collection);

    }// bindCollections

    public void unbindCollections(@Min(0) long categoryId,
            @NotEmpty Set<@NotNull @Min(0) Long> collectionIds) {

        // verify that Category with the id specified already exists
        Optional<Category> ctgr = categoryRepository.findById(categoryId);
        if (ctgr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.CATEGORY_NOT_FOUND, "categoryId"));

        Category target = ctgr.get();

        Set<Collection> collectionsToUnbind = new HashSet<>();
        Problem problemCNF = new Problem(ProblemCode.COLLECTION_NOT_FOUND);
        Problem problemCNIC = new Problem(ProblemCode.CATEGORY_NOT_IN_COLLECTION);

        Optional<Collection> cllctn = null;
        for (Long id : collectionIds) {
            // verify that Collection with current id exists
            cllctn = collectionRepository.findById(id);
            if (cllctn.isEmpty()) {
                problemCNF.add(Long.toString(id));
                continue;
            }
            if (!target.getCollections().contains(cllctn.get())) {
                problemCNIC.add(Long.toString(id));
                problemCNIC.add(Long.toString(categoryId));
                continue;
            }
            collectionsToUnbind.add(cllctn.get());
        }

        // check if there has been any problems
        Set<Problem> problemsEncountered = new HashSet<>();
        if (!problemCNF.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCNF);
        if (!problemCNIC.getInvalidFields().isEmpty())
            problemsEncountered.add(problemCNIC);
        if (!problemsEncountered.isEmpty())
            throw new BadRequestException(problemsEncountered);

        // bind bidirectional relations with Collection
        for (Collection collection : collectionsToUnbind)
            target.unbindCollection(collection);

    }// unbindCollections

}// CategoryService
