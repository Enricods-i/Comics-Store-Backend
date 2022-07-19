package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.exceptions.UnavaiableWishList;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;
import im.enricods.ComicsStore.exceptions.WishListAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.WishListNotFoundException;
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
    public List<WishList> showUsersListsByName(@Min(1) long userId, @NotNull @Size(max = 50) String name){
        
        //verify that User specified by userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        return wishListRepository.findByOwnerAndName(resultUser.get(), name);

    }//showUsersWishListByName


    @Transactional(readOnly = true)
    public List<WishList> showAllUsersLists(@Min(1) long userId){

        //verify that User specified by userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        return wishListRepository.findByOwner(resultUser.get());

    }//showAllUsersLists


    public WishList createUsersList(@Min(1) long userId, @Valid WishList wishList){

        //verify that User specified by userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        //verify that WishList specified doesn't already exists
        if(wishListRepository.existsById(wishList.getId()))
            throw new WishListAlreadyExistsException();

        wishList.setOwner(resultUser.get());
        WishList wl = wishListRepository.save(wishList);

        //bind bidirectional relation
        resultUser.get().addWishList(wl);

        return wl;

    }//createList


    public void deleteUsersList(@Min(1) long userId, @Min(1) long wishListId){

        //verify that User specified by userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        //verify that WishList specified exists
        Optional<WishList> resultList = wishListRepository.findById(wishListId);
        if(!resultList.isPresent())
            throw new WishListNotFoundException();

        //verify that WishList specified belongs to User specified
        if(!resultUser.get().getWishLists().contains(resultList.get()))
            throw new UnavaiableWishList();

        WishList target = resultList.get();

        wishListRepository.delete(target);

        //unbind bidirectional relation
        resultUser.get().getWishLists().remove(target);

    }//deleteList


    public void addComicToList(@Min(1) long userId, @Min(1) long comicId, @Min(1) long wishListId){
        
        //verify that User specified by userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        //verify that Comic specified by comicId exists
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();
        
        //verify that WishList specified by wishListId exists
        Optional<WishList> resultList = wishListRepository.findById(wishListId);
        if(!resultList.isPresent())
            throw new WishListNotFoundException();
        
        //verify that WishList specified belongs to User specified
        if(!resultUser.get().getWishLists().contains(resultList.get()))
            throw new UnavaiableWishList();

        resultList.get().getContent().add(resultComic.get());

    }//addComicToList


    public void deleteComicToList(@Min(1) long userId, @Min(1) long comicId, @Min(1) long wishListId){
        
        //verify that User specified by userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        //verify that Comic specified by comicId exists
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();
        
        //verify that WishList specified by wishListId exists
        Optional<WishList> resultList = wishListRepository.findById(wishListId);
        if(!resultList.isPresent())
            throw new WishListNotFoundException();

        //verify that WishList specified belongs to User specified
        if(!resultUser.get().getWishLists().contains(resultList.get()))
            throw new UnavaiableWishList();
        
        resultList.get().getContent().remove(resultComic.get());

    }//addComicToList


    public void updateWishList(@Min(1)long userId, @Valid WishList wishList){

        //verify that User specified by userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        //verify that WishList specified by wishListId exists
        Optional<WishList> resultList = wishListRepository.findById(wishList.getId());
        if(!resultList.isPresent())
            throw new WishListNotFoundException();

        //verify that WishList specified belongs to User specified        
        if(!resultUser.get().getWishLists().contains(resultList.get()))
            throw new UnavaiableWishList();
        
        //set fields that client can't change
        wishList.setCreationDate(resultList.get().getCreationDate());
        wishList.setOwner(resultUser.get());
        //merge
        wishListRepository.save(wishList);

    }//updateWishList

}//WishListService
