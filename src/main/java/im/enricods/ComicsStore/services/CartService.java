package im.enricods.ComicsStore.services;

import java.util.Iterator;
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
import im.enricods.ComicsStore.repositories.ComicRepository;
import im.enricods.ComicsStore.repositories.UserRepository;

@Service
@Transactional
@Validated
public class CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private CartContentRepository cartContentRepository;

    @Transactional(readOnly = true)
    public Cart getByUser(@NotNull @Min(0) long userId) {

        // verify that User with Id specified exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found!");

        return usr.get().getCart();

    }// getByUser

    public void addComic(@NotNull @Min(0) long userId,
            @NotNull @Min(0) long comicId,
            @NotNull @Min(1) int quantity) {

        // verify that a User with userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found!");

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new IllegalArgumentException("Comic " + comicId + " not found!");

        // verify that the quantity specified is avaiable
        if (quantity >= cmc.get().getQuantity())
            throw new IllegalArgumentException("Unavaiable quantity for comic " +
                    cmc.get().getNumber() + " in collection " +
                    cmc.get().getCollection().getName() + " !");

        Cart cart = usr.get().getCart();

        // create new cart-content entry and set all fields
        CartContent comicInCart = new CartContent();
        comicInCart.setId(new CartContentId(usr.get().getId(), comicId));
        comicInCart.setComic(cmc.get());
        comicInCart.setCart(cart);
        comicInCart.setQuantity(quantity);

        // verify that user's Cart doesn't already contain the Comic specified by
        // comicId
        if (cart.getContent().contains(comicInCart))
            throw new IllegalArgumentException("Comic " + comicId + " already exists in your cart!");

        // bind bidirectional relation
        cart.getContent().add(comicInCart);

        cartContentRepository.save(comicInCart);

        // update cart's size
        cart.setSize(cart.getSize() + quantity);

    }// addComic

    public void removeComic(@NotNull @Min(0) long userId, @NotNull @Min(0) long comicId) {

        // verify that User with userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found!");

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new IllegalArgumentException("Comic " + comicId + " not found!");

        Cart cart = usr.get().getCart();

        // verify that user's Cart contains Comic specified by comicId
        Optional<CartContent> target = cartContentRepository.findById(new CartContentId(cart.getId(), comicId));
        if (target.isEmpty())
            throw new IllegalArgumentException("Comic " + comicId + " not found in your cart!");

        // unbind bidirectional relation
        cart.getContent().remove(target.get());
        target.get().setCart(null);

        // update cart's Size
        cart.setSize(cart.getSize() - target.get().getQuantity());

        cartContentRepository.delete(target.get());

    }// removeComic

    public void clear(@Min(0) long userId) {

        // verify that User with userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found!");

        Cart cart = usr.get().getCart();

        CartContent cc = null;
        Iterator<CartContent> it = cart.getContent().iterator();
        while (it.hasNext()) {
            cc = it.next();
            // unbind bidirectional relation with Cart
            cc.setCart(null);
            it.remove();
            cartContentRepository.delete(cc);
        }

        cart.setSize(0);

    }// clear

    public void updateComicQuantity(@NotNull @Min(0) long userId,
            @NotNull @Min(0) long comicId,
            @NotNull @Min(1) int newQuantity) {

        // verify that User with userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new IllegalArgumentException("User " + userId + " not found!");

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new IllegalArgumentException("Comic " + comicId + " not found!");

        Cart usersCart = usr.get().getCart();

        // verify that user's Cart doesn't contain Comic specified by comicId
        Optional<CartContent> comicInCart = cartContentRepository
                .findById(new CartContentId(usersCart.getId(), comicId));
        if (comicInCart.isEmpty())
            throw new IllegalArgumentException("Comic " + comicId + " not found in your cart.");

        // verify that the quantity specified is avaiable
        if (newQuantity > cmc.get().getQuantity())
            throw new IllegalArgumentException("Unavaiable quantity for comic " +
                    cmc.get().getNumber() + " in collection " +
                    cmc.get().getCollection().getName() + ".");

        
        int bias = comicInCart.get().getQuantity() - newQuantity;
        
        comicInCart.get().setQuantity(newQuantity);

        // update the cart's size
        usersCart.setSize(usersCart.getSize() - bias);

    }// updateComicQuantity

}// CartService
