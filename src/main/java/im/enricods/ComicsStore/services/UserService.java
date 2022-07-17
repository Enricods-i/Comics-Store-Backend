package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.entities.User;
import im.enricods.ComicsStore.exceptions.UserNotFoundException;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;


    @Transactional(readOnly = true)
    public User getUserByEmail(String email){

        Optional<User> result = userRepository.findByEmail(email);

        if(result.isPresent())
            return result.get();
        else
            throw new UserNotFoundException();

    }//getUserByEmail


    @Transactional(readOnly = true)
    public List<User> getUsersByName(String firstName, String lastName){

        return userRepository.findByFirstNameOrLastNameAllIgnoreCase(firstName, lastName);

    }//getUsersByName

    
    @Transactional(readOnly = true)
    public List<User> getUsersByCity(String city){

        return userRepository.findByCity(city);

    }//getUsersByCity

    
    @Transactional(readOnly = true)
    public List<User> getUsersByPhoneNumber(String phoneNumber){

        return userRepository.findByPhoneNumber(phoneNumber);

    }//getUsersByPhoneNumber

    
    public User createUser(User user){

        return userRepository.save(user);

    }//createUser

    
    public void updateUser(User user){

        if(!userRepository.existsById(user.getId()))
            throw new UserNotFoundException();

        userRepository.save(user);

    }//createUser

}//UserService
