package im.enricods.ComicsStore.services;

import java.util.Optional;

import javax.validation.constraints.Min;

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
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.ProblemCode;

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
    public Cart getByUser(@Min(0) long userId) {

        // verify that User with Id specified exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.USER_NOT_FOUND, "userId"));

        return usr.get().getCart();

    }// getByUser

    public void addComic(@Min(0) long userId,
            @Min(0) long comicId,
            @Min(1) int quantity) {

        // verify that a User with userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.USER_NOT_FOUND, "userId"));

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND, "comicId"));

        // verify that the quantity specified is avaiable
        if (quantity > cmc.get().getQuantity())
            throw new BadRequestException(new Problem(ProblemCode.COMIC_QUANTITY_UNAVAIABLE, "quantity"));

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
            throw new BadRequestException(new Problem(ProblemCode.COMIC_ALREADY_IN_CART, "comicId"));

        // bind bidirectional relation
        cart.getContent().add(comicInCart);

        cartContentRepository.save(comicInCart);

        // update cart's size
        cart.setSize(cart.getSize() + quantity);

    }// addComic

    public void removeComic(@Min(0) long userId, @Min(0) long comicId) {

        // verify that User with userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.USER_NOT_FOUND, "userId"));

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND, "comicId"));

        Cart cart = usr.get().getCart();

        // verify that user's Cart contains Comic specified by comicId
        Optional<CartContent> target = cartContentRepository.findById(new CartContentId(cart.getId(), comicId));
        if (target.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND_IN_CART, "comicId", "userId"));

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
            throw new BadRequestException(new Problem(ProblemCode.USER_NOT_FOUND, "userId"));

        Cart cart = usr.get().getCart();

        cartContentRepository.deleteAll(cart.getContent());
        cart.getContent().clear();

        cart.setSize(0);

    }// clear

    public void updateComicQuantity(@Min(0) long userId,
            @Min(0) long comicId,
            @Min(1) int newQuantity) {

        // verify that User with userId exists
        Optional<User> usr = userRepository.findById(userId);
        if (usr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.USER_NOT_FOUND, "userId"));

        // verify that a Comic with comicId exists
        Optional<Comic> cmc = comicRepository.findById(comicId);
        if (cmc.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND, "comicId"));

        Cart usersCart = usr.get().getCart();

        // verify that user's Cart doesn't contain Comic specified by comicId
        Optional<CartContent> comicInCart = cartContentRepository
                .findById(new CartContentId(usersCart.getId(), comicId));
        if (comicInCart.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.COMIC_NOT_FOUND_IN_CART, "comicId", "userId"));

        // verify that the quantity specified is avaiable
        if (newQuantity > cmc.get().getQuantity())
            throw new BadRequestException(new Problem(ProblemCode.COMIC_QUANTITY_UNAVAIABLE, "newQuantity"));

        int bias = comicInCart.get().getQuantity() - newQuantity;

        comicInCart.get().setQuantity(newQuantity);

        // update the cart's size
        usersCart.setSize(usersCart.getSize() - bias);

    }// updateComicQuantity

}// CartService
