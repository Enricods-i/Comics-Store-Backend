package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class WishListService {
    
    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComicRepository comicRepository;


    @Transactional(readOnly = true)
    public List<WishList> showUsersListsByName(long userId, String name){
        
        //verify that User specified by userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        return wishListRepository.findByOwnerAndName(resultUser.get(), name);

    }//showUsersWishListByName


    @Transactional(readOnly = true)
    public List<WishList> showAllUsersLists(long userId){

        //verify that User specified by userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        return wishListRepository.findByOwner(resultUser.get());

    }//showAllUsersLists


    public WishList createUsersList(long userId, @Valid WishList wishList){

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


    public void deleteUsersList(long userId, long wishListId){

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


    public void addComicToList(long userId, long comicId, long wishListId){
        
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


    public void deleteComicToList(long userId, long comicId, long wishListId){
        
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


    public void updateWishList(long userId, @Valid WishList wishList){

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
