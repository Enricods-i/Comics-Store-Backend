package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.entities.WishList;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
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
        
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        return wishListRepository.findByUserAndName(resultUser.get(), name);

    }//showUsersWishListByName


    @Transactional(readOnly = true)
    public List<WishList> showAllUsersLists(long userId){

        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        return wishListRepository.findByUser(resultUser.get());

    }//showAllUsersLists


    public WishList createUsersList(long userId, WishList wishList){

        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        if(wishListRepository.existsByName(wishList.getName()))
            throw new WishListAlreadyExistsException();

        WishList wl = wishListRepository.save(wishList);
        resultUser.get().addWishList(wl);
        return wl;

    }//createList


    public void addComicToList(long comicId, long wishListId){
        
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();
        
        Optional<WishList> resultList = wishListRepository.findById(wishListId);
        if(!resultList.isPresent())
            throw new WishListNotFoundException();
        
        resultList.get().addComic(resultComic.get());

    }//addComicToList


    public void updateWishList(WishList wishList){

        if(!wishListRepository.existsByName(wishList.getName()))
            throw new WishListNotFoundException();
        
        //merge
        wishListRepository.save(wishList);

    }//updateWishList

}//WishListService
