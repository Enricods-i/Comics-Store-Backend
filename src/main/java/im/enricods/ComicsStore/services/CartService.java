package im.enricods.ComicsStore.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.entities.CartContent;
import im.enricods.ComicsStore.entities.CartContentId;
import im.enricods.ComicsStore.entities.Comic;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.exceptions.CartNotFoundException;
import im.enricods.ComicsStore.exceptions.ComicAlreadyExistsException;
import im.enricods.ComicsStore.exceptions.ComicNotFoundException;
import im.enricods.ComicsStore.exceptions.ComicNotInCartException;
import im.enricods.ComicsStore.exceptions.ComicsQuantityUnavaiableException;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;
import im.enricods.ComicsStore.repositories.CartContentRepository;
import im.enricods.ComicsStore.repositories.CartRepository;
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.repositories.UserRepository;

@Service
@Transactional
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
    public Cart getCartByUser(long userId){

        //verify that User with Id specified exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        //verify that User has a Cart
        Optional<Cart> resultCart = cartRepository.findByUser(resultUser.get());
        if(!resultCart.isPresent())
            throw new CartNotFoundException();
        
        return resultCart.get();

    }//getUsersCart


    public void addComicToUsersCart(long userId, long comicId, int quantity){

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
        usersCart.setSize(usersCart.getSize()+1);

    }//addComicToUsersCart


    public void deleteComicFromUsersCart(long userId, long comicId){

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
        usersCart.setSize(usersCart.getSize()-1);

    }//deleteComicFromUsersCart


    public void updateComicQuantity(long userId, long comicId, int newQuantity){

        //verify that User with userId exists
        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        //verify that Comic with comicId exists
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();

        long cartId = resultUser.get().getCart().getId();

        //verify that user's Cart contains Comic specified by comicId 
        Optional<CartContent> comicInCart = cartContentRepository.findById(new CartContentId(cartId, comicId));
        if(!comicInCart.isPresent())
            throw new ComicNotInCartException();

        //verify that the quantity specified is avaiable
        if(newQuantity > resultComic.get().getQuantity())
            throw new ComicsQuantityUnavaiableException();
        
        comicInCart.get().setQuantity(newQuantity);

    }//updateComicsQuantity

}//CartService
