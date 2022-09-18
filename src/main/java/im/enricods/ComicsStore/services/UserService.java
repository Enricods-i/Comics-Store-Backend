package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.repositories.CartRepository;
import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.utils.BadRequestException;
import im.enricods.ComicsStore.utils.Problem;
import im.enricods.ComicsStore.utils.ProblemCode;
import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.entities.User;

@Service
@Transactional
@Validated
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Transactional(readOnly = true)
    public User getByEmail(@NotNull @Email String email) {

        Optional<User> usr = userRepository.findByEmail(email);
        if (usr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.USER_NOT_FOUND, "email"));
        return usr.get();

    }// getByEmail

    @Transactional(readOnly = true)
    public List<User> getByName(@Size(min = 1, max = 20) String firstName, @Size(min = 1, max = 20) String lastName) {

        return userRepository.findByFirstNameOrLastNameAllIgnoreCase(firstName, lastName);

    }// getByName

    @Transactional(readOnly = true)
    public List<User> getByCity(@NotNull @Size(min = 2, max = 20) String city) {

        return userRepository.findByCity(city);

    }// getByCity

    @Transactional(readOnly = true)
    public List<User> getByPhoneNumber(@NotNull @Size(min = 6, max = 20) String phoneNumber) {

        return userRepository.findByPhoneNumber(phoneNumber);

    }// getByPhoneNumber

    public User add(@NotNull @Valid User user) {

        // verify that User specified doesn't already exist
        if (userRepository.existsByEmail(user.getEmail()))
            throw new BadRequestException(new Problem(ProblemCode.USER_ALREADY_EXISTS, "user"));

        // create user's cart
        Cart cart = new Cart();
        // persist cart
        cart = cartRepository.save(cart);

        // bind cart
        cart.bindToUser(user);

        // persist user
        User result = userRepository.save(user);

        return result;

    }// add

    public User modify(@NotNull @Valid User user) {

        // verify that User specified exists
        Optional<User> usr = userRepository.findById(user.getId());
        if (usr.isEmpty())
            throw new BadRequestException(new Problem(ProblemCode.USER_NOT_FOUND, "user"));

        // if modifying email verify that does not exist a User with the same email
        if (!user.getEmail().equals(usr.get().getEmail()) && userRepository.existsByEmail(user.getEmail()))
            throw new BadRequestException(new Problem(ProblemCode.USER_ALREADY_EXISTS, "user"));

        // set fields that client can't modify
        user.setCart(usr.get().getCart());
        user.setCreationDate(usr.get().getCreationDate());

        // merge
        return userRepository.save(user);

    }// modify

}// UserService
