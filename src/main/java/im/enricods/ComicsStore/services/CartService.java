package im.enricods.ComicsStore.services;

import java.util.Optional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.entities.CartContent;
import im.enricods.ComicsStore.entities.CartContentId;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.repositories.CartContentRepository;
import im.enricods.ComicsStore.repositories.CartRepository;
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.utils.exceptions.CartNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.ComicAlreadyExistsException;
import im.enricods.ComicsStore.utils.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.utils.exceptions.ComicNotInCartException;
import im.enricods.ComicsStore.utils.exceptions.ComicsQuantityUnavaiableException;
import im.enricods.ComicsStore.utils.exceptions.UserNotFoundException;

@Service
@Transactional
@Validated
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private CartContentRepository cartContentRepository;


    @Transactional(readOnly = true)
    public Cart getCartByUser(@NotNull @Min(1) long userId){

        //verify that User with Id specified exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        //get the User's Cart
        Optional<Cart> resultCart = cartRepository.findByUser(resultUser.get());
        if(!resultCart.isPresent())
            throw new CartNotFoundException();

        return resultCart.get();

    }//getUsersCart


    public void addComicToUsersCart(@NotNull @Min(1) long userId, @NotNull @Min(1) long comicId, @NotNull @Min(1) int quantity){

        //verify that a User with userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        //verify that a Comic with comicId exists
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();
        
        //verify that the quantity specified is avaiable
        if(quantity > resultComic.get().getQuantity())
            throw new ComicsQuantityUnavaiableException("Unavaiable quantity for comic "+ resultComic.get().getNumber()+ " in collection "+ resultComic.get().getCollection().getName() +" !");
        
        Cart usersCart = resultUser.get().getCart();

        //create new cart entry and set all fields
        CartContent newComicInCart = new CartContent();
        newComicInCart.setId(new CartContentId(usersCart.getId(), comicId));
        newComicInCart.setComic(resultComic.get());
        newComicInCart.setCart(usersCart);
        newComicInCart.setQuantity(quantity);

        //verify that user's Cart doesn't contains the Comic specified by comicId yet
        if(usersCart.getContent().contains(newComicInCart))
            throw new ComicAlreadyExistsException();
        
        cartContentRepository.save(newComicInCart);
        
        //bind bidirectional relation and update cart's Size
        usersCart.getContent().add(newComicInCart);
        usersCart.setSize(usersCart.getSize()+quantity);

    }//addComicToUsersCart


    public void deleteComicFromUsersCart(@NotNull @Min(1) long userId, @NotNull @Min(1) long comicId){

        //verify that User with userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        //verify that Comic with comicId exists
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();
        
        Cart usersCart = resultUser.get().getCart();

        //verify that user's Cart contains Comic specified by comicId 
        Optional<CartContent> target = cartContentRepository.findById(new CartContentId(usersCart.getId(), comicId));
        if(!target.isPresent())
            throw new ComicNotInCartException();
        
        cartContentRepository.delete(target.get());

        //unbind bidirectional relation
        usersCart.getContent().remove(target.get());
        //update cart's Size
        usersCart.setSize(usersCart.getSize()-target.get().getQuantity());

    }//deleteComicFromUsersCart


    public void updateComicQuantity(@NotNull @Min(1) long userId, @NotNull @Min(1) long comicId, @NotNull @Min(1) int newQuantity){

        //verify that User with userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        //verify that Comic with comicId exists
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();

        Cart usersCart = resultUser.get().getCart();

        //verify that user's Cart contains Comic specified by comicId 
        Optional<CartContent> comicInCart = cartContentRepository.findById(new CartContentId(usersCart.getId(), comicId));
        if(!comicInCart.isPresent())
            throw new ComicNotInCartException();

        //verify that the quantity specified is avaiable
        if(newQuantity > resultComic.get().getQuantity())
            throw new ComicsQuantityUnavaiableException();
        
        comicInCart.get().setQuantity(newQuantity);

        //update the cart's size
        int bias = usersCart.getSize() - newQuantity;
        usersCart.setSize(usersCart.getSize() - bias);

    }//updateComicsQuantity

}//CartService
