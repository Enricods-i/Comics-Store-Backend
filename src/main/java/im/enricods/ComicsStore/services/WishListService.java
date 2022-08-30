package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<WishList> getByOwnerAndName(@Min(0) long userId, @NotNull @Size(max = 50) String name){
        
        //verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+userId+" not found!");
        
        return wishListRepository.findByOwnerAndNameContaining(usr.get(), name);

    }//getByOwnerAndName


    @Transactional(readOnly = true)
    public List<WishList> getAllByUser(@Min(0) long userId){

        //verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+userId+" not found!");
        
        return wishListRepository.findByOwner(usr.get());

    }//getAllByUser


    public WishList add(@Min(0) long userId, @Valid WishList wishList){

        //verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+userId+" not found!");

        WishList wl = wishListRepository.save(wishList);

        //bind bidirectional relation
        usr.get().addWishList(wl);

        return wl;

    }//add


    public void remove(@Min(0) long userId, @Min(0) long wishListId){

        //verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+userId+" not found!");

        //verify that WishList specified exists
        Optional<WishList> list = wishListRepository.findById(wishListId);
        if(list.isEmpty())
            throw new IllegalArgumentException("Wish list "+wishListId+" not found!");

        //verify that WishList specified belongs to User specified
        if(!usr.get().getWishLists().contains(list.get()))
            throw new IllegalArgumentException("The wish list "+wishListId+" does not belong to the user "+userId);

        WishList target = list.get();

        wishListRepository.delete(target);

        //unbind bidirectional relation
        usr.get().removeWishList(target);

    }//deleteList


    public void chName(@Min(0) long userId, @Min(0) long wishListId, @NotNull @Size(min = 1, max = 30) String newName){

        //verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+userId+" not found!");

        //verify that WishList specified exists
        Optional<WishList> list = wishListRepository.findById(wishListId);
        if(list.isEmpty())
            throw new IllegalArgumentException("Wish list "+wishListId+" not found!");

        //verify that WishList specified belongs to User specified
        if(!usr.get().getWishLists().contains(list.get()))
            throw new IllegalArgumentException("The wish list "+wishListId+" does not belong to the user "+userId);

        list.get().setName(newName);

    }//chName


    public void addComics(@Min(0) long userId, @Min(0) long wishListId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds){
        
        //verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+userId+" not found!");

        //verify that WishList specified exists
        Optional<WishList> list = wishListRepository.findById(wishListId);
        if(list.isEmpty())
            throw new IllegalArgumentException("Wish list "+wishListId+" not found!");

        //verify that WishList specified belongs to User specified
        if(!usr.get().getWishLists().contains(list.get()))
            throw new IllegalArgumentException("The wish list "+wishListId+" does not belong to the user "+userId);

        Optional<Comic> cmc = null;
        for(long id : comicIds){
            cmc = comicRepository.findById(id);
            if(cmc.isPresent())
                list.get().getContent().add(cmc.get());
        }

    }//addComics


    public void removeComics(@Min(0) long userId, @Min(0) long wishListId, @NotEmpty Set<@NotNull @Min(0) Long> comicIds){
        
        //verify that User specified by userId exists
        Optional<User> usr = userRepository.findById(userId);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+userId+" not found!");

        //verify that WishList specified exists
        Optional<WishList> list = wishListRepository.findById(wishListId);
        if(list.isEmpty())
            throw new IllegalArgumentException("Wish list "+wishListId+" not found!");

        //verify that WishList specified belongs to User specified
        if(!usr.get().getWishLists().contains(list.get()))
            throw new IllegalArgumentException("The wish list "+wishListId+" does not belong to the user "+userId);
        
        Comic cmc = new Comic();
        for(long id : comicIds){
            cmc.setId(id);
            list.get().getContent().remove(cmc);
        }

    }//removeComics


}//WishListService
