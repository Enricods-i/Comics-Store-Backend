package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.enricods.ComicsStore.repositories.CartRepository;
import im.enricods.ComicsStore.repositories.UserRepository;
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
    public User getByEmail(@Email String email){

        Optional<User> usr = userRepository.findByEmail(email);
        if(usr.isEmpty())
            throw new IllegalArgumentException("User with email \""+email+"\" not found!");
        return usr.get();

    }//getByEmail


    @Transactional(readOnly = true)
    public List<User> getByName(@Size(min=1, max=20) String firstName, @Size(min=1, max=20) String lastName){

        return userRepository.findByFirstNameOrLastNameAllIgnoreCase(firstName, lastName);

    }//getByName

    
    @Transactional(readOnly = true)
    public List<User> getByCity(@Size(min=2, max=20) String city){

        return userRepository.findByCity(city);

    }//getByCity

    
    @Transactional(readOnly = true)
    public List<User> getByPhoneNumber(@Size(min=6, max=20)String phoneNumber){

        return userRepository.findByPhoneNumber(phoneNumber);

    }//getByPhoneNumber

    
    public User add(@Valid User user){

        //verify that User specified doesn't already exist
        if(userRepository.existsByEmail(user.getEmail()))
            throw new IllegalArgumentException("User with email \""+user.getEmail()+"\" already exists!");

        //create user's cart
        Cart cart = new Cart();
        cart = cartRepository.save(cart);

        user.setId(0);

        //persist
        User result = userRepository.save(user);

        //bind cart
        cart.bindToUser(result);

        return result;

    }//add

    
    public void modify(@Valid User user){

        //verify that User specified exists
        Optional<User> usr = userRepository.findById(user.getId());
        if(usr.isEmpty())
            throw new IllegalArgumentException("User "+user.getId()+" not found!");

        //if modifying email verify that does not exist a User with the same email
        if( !user.getEmail().equals(usr.get().getEmail()) && userRepository.existsByEmail(usr.get().getEmail()) )
            throw new IllegalArgumentException("User with email \""+user.getEmail()+"\" already exists!");

        //set fields that client can't modify
        user.setCart(usr.get().getCart());
        user.setCreationDate(usr.get().getCreationDate());

        //merge
        userRepository.save(user);

    }//modify

}//UserService
