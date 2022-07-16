package im.enricods.ComicsStore.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.enricods.ComicsStore.repositories.UserRepository;
import im.enricods.ComicsStore.entities.User;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;


    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email){

        return userRepository.findByEmail(email);

    }//getUserByEmail


    @Transactional(readOnly = true)
    public List<User> getUsersByName(String firstName, String lastName){

        return userRepository.findByFirstnameOrLastnameAllIgnoreCase(firstName, lastName);

    }//getUsersByName

    
    @Transactional(readOnly = true)
    public List<User> getUsersByCity(String city){

        return userRepository.findByCity(city);

    }//getUsersByCity

    
    @Transactional(readOnly = true)
    public List<User> getUsersByPhoneNumber(String phoneNumber){

        return userRepository.findByPhoneNumber(phoneNumber);

    }//getUsersByPhoneNumber

    //add User

    //update User

}//UserService
