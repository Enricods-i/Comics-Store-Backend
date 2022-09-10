package im.enricods.ComicsStore.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.repositories.WishListRepository;

@Service
@Transactional
@Validated
public class WishListService {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Transactional(readOnly = true)
    public List<WishList> getByOwnerAndName(@Min(0) long userId, @NotNull @Size(min = 3, max = 30) String name) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found.");

        return wishListRepository.findByOwnerAndNameContaining(usr.get(), name);

    }// getByOwnerAndName

    @Transactional(readOnly = true)
    public List<WishList> getAllByUser(@Min(0) long userId) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found.");

        return wishListRepository.findByOwner(usr.get());

    }// getAllByUser

    public WishList add(@Min(0) long userId, @NotNull @Size(min = 3, max = 30) String name) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found.");

        // verify that does not already exist a wish list with name "name" belonging to
        // the user "usr"
        if (wishListRepository.existsByOwnerAndName(usr.get(), name))
            throw new IllegalArgumentException("A wish list with name \"" + name + "\" already exists.");

        WishList list = new WishList();
        list.setName(name);
        // bind bidirectional relation with User
        usr.get().addWishList(list);

        return wishListRepository.save(list);

    }// add

    public List<Comic> getContent(
            @Min(0) long userId,
            @Min(0) long wishListId,
            @Min(0) int pageNumber,
            @Min(0) int pageSize,
            @NotNull String sortBy) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found.");

        // verify that WishList specified exists
        Optional<WishList> list = wishListRepository.findById(wishListId);
        if (list.isEmpty())
            throw new IllegalArgumentException("Wish list " + wishListId + " not found.");

        // verify that WishList "list" belongs to User "usr"
        if (!usr.get().getWishLists().contains(list.get()))
            throw new IllegalArgumentException(
                    "The wish list " + wishListId + " does not belong to the user " + userId + ".");

        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Comic> pagedResult = wishListRepository.getContent(list.get(), paging);
        return pagedResult.getContent();

    }// getContent

    public void remove(@Min(0) long userId, @Min(0) long wishListId) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found.");

        // verify that WishList specified exists
        Optional<WishList> list = wishListRepository.findById(wishListId);
        if (list.isEmpty())
            throw new IllegalArgumentException("Wish list " + wishListId + " not found.");

        // verify that WishList "list" belongs to User "usr"
        if (!usr.get().getWishLists().contains(list.get()))
            throw new IllegalArgumentException(
                    "The wish list " + wishListId + " does not belong to the user " + userId + ".");

        WishList target = list.get();

        // unbind bidirectional relation with User
        usr.get().removeWishList(target);

        wishListRepository.delete(target);

    }// remove

    public void changeName(@Min(0) long userId, @Min(0) long wishListId,
            @NotNull @Size(min = 1, max = 30) String newName) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found.");

        // verify that WishList specified exists
        Optional<WishList> list = wishListRepository.findById(wishListId);
        if (list.isEmpty())
            throw new IllegalArgumentException("Wish list " + wishListId + " not found.");

        // verify that WishList specified belongs to User specified
        if (!usr.get().getWishLists().contains(list.get()))
            throw new IllegalArgumentException(
                    "The wish list " + wishListId + " does not belong to the user " + userId + ".");

        list.get().setName(newName);

    }// changeName

    public void addComics(@Min(0) long userId, @Min(0) long wishListId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found.");

        // verify that WishList specified exists
        Optional<WishList> list = wishListRepository.findById(wishListId);
        if (list.isEmpty())
            throw new IllegalArgumentException("Wish list " + wishListId + " not found.");

        // verify that WishList specified belongs to User specified
        if (!usr.get().getWishLists().contains(list.get()))
            throw new IllegalArgumentException(
                    "The wish list " + wishListId + " does not belong to the user " + userId + ".");

        WishList target = list.get();

        Set<Comic> comicsToAdd = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Comic> cmc = null;
        for (long id : comicIds) {
            cmc = comicRepository.findById(id);
            if (cmc.isEmpty()) {
                problemsEncountered.append("Comic " + id + " not found.\n");
                continue;
            }
            if (target.getContent().contains(cmc.get())) {
                problemsEncountered.append("Comic: " + id + " is already in your wish list " + wishListId + ".\n");
                continue;
            }
            comicsToAdd.add(cmc.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // bind bidirectional relations with Comic
        for (Comic comic : comicsToAdd)
            target.getContent().add(comic);

    }// addComics

    public void removeComics(@Min(0) long userId, @Min(0) long wishListId,
            @NotEmpty Set<@NotNull @Min(0) Long> comicIds) {

        // verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found.");

        // verify that WishList specified exists
        Optional<WishList> list = wishListRepository.findById(wishListId);
        if (list.isEmpty())
            throw new IllegalArgumentException("Wish list " + wishListId + " not found.");

        // verify that WishList specified belongs to User specified
        if (!usr.get().getWishLists().contains(list.get()))
            throw new IllegalArgumentException(
                    "The wish list " + wishListId + " does not belong to the user " + userId + ".");

        WishList target = list.get();

        Set<Comic> comicsToRemove = new HashSet<>();
        StringBuilder problemsEncountered = new StringBuilder();

        Optional<Comic> cmc = null;
        for (long id : comicIds) {
            cmc = comicRepository.findById(id);
            if (cmc.isEmpty()) {
                problemsEncountered.append("Comic " + id + " not found.\n");
                continue;
            }
            if (!target.getContent().contains(cmc.get())) {
                problemsEncountered.append("Wish list: " + wishListId + " does not contains comic " + id + ".\n");
                continue;
            }
            comicsToRemove.add(cmc.get());
        }

        // check if there has been any problems
        if (problemsEncountered.length() > 0)
            throw new IllegalArgumentException(problemsEncountered.append("Operation canceled.").toString());

        // unbind bidirectional relations with Comic
        for (Comic comic : comicsToRemove)
            target.getContent().remove(comic);

    }// removeComics

}// WishListService
