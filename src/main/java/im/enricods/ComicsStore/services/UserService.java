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

import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.entities.Cart;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.exceptions.UserAlreadyExists;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;

@Service
@Transactional
@Validated
public class UserService {
    
    @Autowired
    private UserRepository userRepository;


    @Transactional(readOnly = true)
    public User getUserByEmail(@Email String email){

        Optional<User> result = userRepository.findByEmail(email);
        if(result.isPresent())
            return result.get();
        else
            throw new UserNotFoundException();

    }//getUserByEmail


    @Transactional(readOnly = true)
    public List<User> getUsersByName(@Size(min=1, max=20) String firstName, @Size(min=1, max=20) String lastName){

        return userRepository.findByFirstNameOrLastNameAllIgnoreCase(firstName, lastName);

    }//getUsersByName

    
    @Transactional(readOnly = true)
    public List<User> getUsersByCity(@Size(min=2, max=20) String city){

        return userRepository.findByCity(city);

    }//getUsersByCity

    
    @Transactional(readOnly = true)
    public List<User> getUsersByPhoneNumber(@Size(min=6, max=20)String phoneNumber){

        return userRepository.findByPhoneNumber(phoneNumber);

    }//getUsersByPhoneNumber

    
    public User createUser(@Valid User user){

        //verify that User specified doesn't already exists
        if(userRepository.existsByEmail(user.getEmail()))
            throw new UserAlreadyExists();

        //create user's cart
        Cart usersCart = new Cart();
        user.addCart(usersCart);

        //persist - cart via cascade type persist
        return userRepository.save(user);

    }//createUser

    
    public void updateUser(@Valid User user){

        //verify that User specified exists
        Optional<User> resultUser = userRepository.findById(user.getId());
        if(!resultUser.isPresent())
            throw new UserNotFoundException();

        //set fields that client don't know
        user.setCart(resultUser.get().getCart());
        user.setCreationDate(resultUser.get().getCreationDate());

        //merge
        userRepository.save(user);

    }//createUser

}//UserService
