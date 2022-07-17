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
    public Cart getUsersCart(long userId){

        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        Optional<Cart> resultCart = cartRepository.findByUser(resultUser.get());
        if(!resultCart.isPresent())
            throw new CartNotFoundException();
        
        float amount = 0;
        Cart result = resultCart.get();
        for(CartContent cc : result.getContent()){
            amount += cc.getComic().getCollection().getPrice();
        }

        result.setTotal(amount);
        return result;

    }//getUsersCart

    /*
    public void createUsersCart(long userId){

        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        Cart cart = new Cart();
        cartRepository.save(cart);
        resultUser.get().setCart(cart);

    }//createUsersCart
    */


    public void addComicToUsersCart(long userId, long comicId, int quantity){

        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();
        
        if(quantity > resultComic.get().getQuantity())
            throw new ComicsQuantityUnavaiableException("Unavaiable quantity for comic "+ resultComic.get().getNumber()+ " in collection "+ resultComic.get().getCollection().getName() +" !");
        
        Cart usersCart = resultUser.get().getCart();

        CartContent newComicInCart = new CartContent();
        newComicInCart.setComic(resultComic.get());
        newComicInCart.setCart(usersCart);
        newComicInCart.setQuantity(quantity);

        if(usersCart.getContent().contains(newComicInCart))
            throw new ComicAlreadyExistsException();
        
        cartContentRepository.save(newComicInCart);
        
        usersCart.getContent().add(newComicInCart);
        usersCart.setSize(usersCart.getSize()+1);

    }//addComicToUsersCart


    public void deleteComicFromUsersCart(long userId, long comicId){

        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();
        
        Cart usersCart = resultUser.get().getCart();

        Optional<CartContent> target = cartContentRepository.findById(new CartContentId(usersCart.getId(), comicId));
        if(!target.isPresent())
            throw new ComicNotInCartException();
        
        cartContentRepository.delete(target.get());

        //con cascade type remove rimuovo la relazione con Cart
        usersCart.setSize(usersCart.getSize()-1);

    }//deleteComicFromUsersCart


    public void updateComicsQuantity(long userId, long comicId, int newQuantity){

        Optional<User> resultUser = userRepository.findById(userId);
        if(!resultUser.isPresent())
            throw new UserNotFoundException();
        
        Optional<Comic> resultComic = comicRepository.findById(comicId);
        if(!resultComic.isPresent())
            throw new ComicNotFoundException();

        long cartId = resultUser.get().getCart().getId();
        Optional<CartContent> comicInCart = cartContentRepository.findById(new CartContentId(cartId, comicId));
        if(!comicInCart.isPresent())
            throw new ComicNotInCartException();

        if(newQuantity > resultComic.get().getQuantity())
            throw new ComicsQuantityUnavaiableException();
        
        comicInCart.get().setQuantity(newQuantity);

    }//updateComicsQuantity

}//CartService
